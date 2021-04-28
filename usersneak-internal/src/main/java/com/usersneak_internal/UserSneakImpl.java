package com.usersneak_internal;

import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.usersneak_api.SurveyResultsHandler;
import com.usersneak_api.UserSneakApi;
import com.usersneak_api.UserSneakApi.StatusCallback.SurveyStatus;
import com.usersneak_internal.models.Survey;
import com.usersneak_internal.remote.sheets.repo.SheetsModule;
import com.usersneak_internal.remote.usersneak.repo.UserSneakModule;
import com.usersneak_internal.ui.UserSneakSurveyActivity;
import com.usersneak_internal.utils.network.RequestStatus;
import com.usersneak_internal.utils.network.RequestStatus.Status;

public final class UserSneakImpl implements UserSneakApi {

  @Override
  public UserSneakApi configureUserSneakApiKey(String apiKey) {
    UserSneakModule.getInstance().setApiKey(apiKey);
    return this;
  }

  @Override
  public UserSneakApi configureSheetsId(String sheetId) {
    UserSneakModule.getInstance().setSheetId(sheetId);
    return this;
  }

  @Override
  public UserSneakApi configureSurveyResultsHandler(SurveyResultsHandler handler) {
    UserSneakModule.getInstance().setSurveyResultHandler(handler);
    return this;
  }

  @Override
  public UserSneakApi configureCustomerId(String id) {
    UserSneakModule.getInstance().setCustomerId(id);
    return this;
  }

  @Override
  public UserSneakApi configureResurveyWindowMillis(long millis) {
    UserSneakModule.getInstance().setResurveyWindow(millis);
    return this;
  }

  @Override
  public void preTrack(String event) {
    SheetsModule.getInstance().preWarmSurvey(event);
  }

  @Override
  public void track(String event, StatusCallback statusCallback) {
    LiveData<RequestStatus<Optional<Survey>>> liveData =
        SheetsModule.getInstance().getSurvey(event);
    liveData.observeForever(
        new Observer<RequestStatus<Optional<Survey>>>() {
          @Override
          public void onChanged(RequestStatus<Optional<Survey>> status) {
            if (status.status == Status.INITIAL || status.status == Status.PENDING) {
              return;
            }

            if (status.status == Status.FAILED) {
              if (status.getError() instanceof RemoteException) {
                Log.d("UserSneak", "FAILURE: no survey");
                statusCallback.handleStatus(SurveyStatus.NO_SURVEY);
              } else {
                Log.d("UserSneak", "FAILURE: survey malformed");
                statusCallback.handleStatus(SurveyStatus.SURVEY_MALFORMED);
              }
              liveData.removeObserver(this);
              return;
            }

            if (status.status == Status.SUCCESS) {
              if (status.getResult().isPresent()) {
                Log.d("UserSneak", "SUCCESS: Survey Available");
                statusCallback.handleStatus(SurveyStatus.AVAILABLE);
              } else {
                Log.d("UserSneak", "SUCCESS: No Survey Active");
                statusCallback.handleStatus(SurveyStatus.NO_SURVEY);
              }
              liveData.removeObserver(this);
              return;
            }
            throw new IllegalStateException("Unhandled state: " + status.status);
          }
        });
  }

  @Override
  public void showSurvey(
      FragmentActivity activity, String event, ActivityResultLauncher<Intent> launcher) {
    Log.d("UserSneak", "Launching Survey");
    launcher.launch(UserSneakSurveyActivity.create(activity, event));
  }

  @Override
  public void logout(boolean clearResurveyWindow) {}

  @Override
  public void getAllEvents(AllEventsCallback callback) {
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
  }
}
