package com.musurveys_internal;

import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.musurveys_api.SurveyResultsHandler;
import com.musurveys_api.MuSurveysApi;
import com.musurveys_api.MuSurveysApi.StatusCallback.SurveyStatus;
import com.musurveys_internal.models.Survey;
import com.musurveys_internal.remote.sheets.repo.SheetsModule;
import com.musurveys_internal.remote.musurveys.repo.MuSurveysModule;
import com.musurveys_internal.ui.MuSurveysActivity;
import com.musurveys_internal.utils.network.RequestStatus;
import com.musurveys_internal.utils.network.RequestStatus.Status;

public final class MuSurveysImpl implements MuSurveysApi {

  @Override
  public MuSurveysApi configureMuSurveysApiKey(String apiKey) {
    safeCall(() -> {
      MuSurveysModule.getInstance().setApiKey(apiKey);
      return null;
    });
    return this;
  }

  @Override
  public MuSurveysApi configureSheetsId(String sheetId) {
    safeCall(() -> {
      MuSurveysModule.getInstance().setSheetId(sheetId);
      return null;
    });
    return this;
  }

  @Override
  public MuSurveysApi configureSurveyResultsHandler(SurveyResultsHandler handler) {
    safeCall(() -> {
      MuSurveysModule.getInstance().setSurveyResultHandler(handler);
      return null;
    });
    return this;
  }

  @Override
  public MuSurveysApi configureCustomerId(String id) {
    safeCall(() -> {
      MuSurveysModule.getInstance().setCustomerId(id);
      return null;
    });
    return this;
  }

  @Override
  public MuSurveysApi configureResurveyWindowMillis(long millis) {
    safeCall(() -> {
      MuSurveysModule.getInstance().setResurveyWindow(millis);
      return null;
    });
    return this;
  }

  @Override
  public void preTrack(String event) {
    safeCall(() -> {
      MuSurveysModule.getInstance().preWarmSurvey(event);
      return null;
    });
  }

  @Override
  public void track(String event, StatusCallback statusCallback) {
    boolean crashed = safeCall(() -> {
      LiveData<RequestStatus<Optional<Survey>>> liveData =
          MuSurveysModule.getInstance().getSurvey(event);
      liveData.observeForever(
          new Observer<RequestStatus<Optional<Survey>>>() {
            @Override
            public void onChanged(RequestStatus<Optional<Survey>> status) {
              if (status.status == Status.INITIAL || status.status == Status.PENDING) {
                return;
              }

              if (status.status == Status.FAILED) {
                if (status.getError() instanceof RemoteException) {
                  Log.d("MuSurveys", "FAILURE: no survey");
                  statusCallback.handleStatus(SurveyStatus.NO_SURVEY);
                } else {
                  Log.d("MuSurveys", "FAILURE: survey may be malformed");
                  statusCallback.handleStatus(SurveyStatus.NO_SURVEY);
                }
                liveData.removeObserver(this);
                return;
              }

              if (status.status == Status.SUCCESS) {
                if (status.getResult().isPresent()) {
                  Log.d("MuSurveys", "SUCCESS: Survey Available");
                  statusCallback.handleStatus(SurveyStatus.AVAILABLE);
                } else {
                  Log.d("MuSurveys", "SUCCESS: No Survey Active");
                  statusCallback.handleStatus(SurveyStatus.NO_SURVEY);
                }
                liveData.removeObserver(this);
                return;
              }
              throw new IllegalStateException("Unhandled state: " + status.status);
            }
          });
      return null;
    });
    if (crashed) {
      statusCallback.handleStatus(SurveyStatus.NO_SURVEY);
    }
  }

  @Override
  public void showSurvey(
      FragmentActivity activity, String event, ActivityResultLauncher<Intent> launcher) {
    safeCall(() -> {
      Log.d("MuSurveys", "Launching Survey");
      launcher.launch(MuSurveysActivity.create(activity, event));
      return null;
    });
  }

  @Override
  public void logout() {
    safeCall(() -> {
      MuSurveysModule.getInstance().logout();
      return null;
    });
  }

  @Override
  public void getAllEvents(AllEventsCallback callback) {
    safeCall(() -> {
      LiveData<RequestStatus<ImmutableList<String>>> liveData =
          SheetsModule.getInstance().getEventNames();
      liveData.observeForever(
          new Observer<RequestStatus<ImmutableList<String>>>() {
            @Override
            public void onChanged(RequestStatus<ImmutableList<String>> status) {
              switch (status.status) {
                case INITIAL:
                case PENDING:
                  break;
                case SUCCESS:
                  callback.handleEventsReady(status.getResult());
                  liveData.removeObserver(this);
                  break;
                case FAILED:
                  // TODO(allen): Consider not calling this
                  callback.handleEventsReady(ImmutableList.of());
                  liveData.removeObserver(this);
                  break;
              }
            }
          });
      return null;
    });
  }

  private <T> boolean safeCall(Supplier<T> supplier) {
    try {
      supplier.get();
    } catch (Exception e) {
      try {
        String stack = stackTrace(e.getStackTrace());
        Log.e("MuSurveys", stack);
        MuSurveysModule.getInstance().logError(stack);
      } catch (Exception ignored) {}
      return true;
    }
    return false;
  }

  private String stackTrace(StackTraceElement[] stackTrace) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5 || i < stackTrace.length; i++) {
      sb.append(stackTrace[i].toString()).append("\n");
    }
    return sb.toString();
  }
}
