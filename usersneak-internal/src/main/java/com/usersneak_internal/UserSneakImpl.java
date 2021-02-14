package com.usersneak_internal;

import android.content.Context;
import android.os.RemoteException;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AppCompatActivity;
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
import com.usersneak_internal.utils.network.RequestStatus;
import com.usersneak_internal.utils.network.RequestStatus.Status;

public final class UserSneakImpl implements UserSneakApi {

  @Override
  public UserSneakApi configureUserSneakApiKey(Context context, String apiKey) {
    return this;
  }

  @Override
  public UserSneakApi configureSheetsApi(Context context, String sheetId) {
    // TODO(allen): store this sheet ID and use it !
    return this;
  }

  @Override
  public UserSneakApi configureSurveyResultsHandler(Context context, SurveyResultsHandler handler) {
    // TODO(allen): store this handler and deliver the survey results back to the client
    return this;
  }

  @Override
  public UserSneakApi configureResurveyWindowMillis(long millis) {
    // TODO(allen): store this value and use it !
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
                statusCallback.handleStatus(SurveyStatus.NO_SURVEY);
              } else {
                statusCallback.handleStatus(SurveyStatus.SURVEY_MALFORMED);
              }
              liveData.removeObserver(this);
              return;
            }

            if (status.status == Status.SUCCESS) {
              if (status.getResult().isPresent()) {
                statusCallback.handleStatus(SurveyStatus.AVAILABLE);
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
      FragmentActivity activity,
      String event,
      ActivityResultCallback<ActivityResult> resultCallback) {
    // TODO(allen): Actually show the survey
    resultCallback.onActivityResult(new ActivityResult(AppCompatActivity.RESULT_CANCELED, null));
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
