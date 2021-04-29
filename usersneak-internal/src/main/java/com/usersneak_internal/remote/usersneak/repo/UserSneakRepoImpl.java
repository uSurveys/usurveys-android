package com.usersneak_internal.remote.usersneak.repo;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.usersneak_api.SurveyResults;
import com.usersneak_api.SurveyResultsHandler;
import com.usersneak_api.UserSneakQuestion;
import com.usersneak_internal.models.Survey;
import com.usersneak_internal.remote.usersneak.api.UserSneakServiceGenerator;
import com.usersneak_internal.remote.usersneak.api.models.GetSurveyResponse;
import com.usersneak_internal.remote.usersneak.api.models.PostSurveyResultBody;
import com.usersneak_internal.remote.usersneak.cache.UserSneakConfigCache;
import com.usersneak_internal.utils.RequestStatusLiveData;
import com.usersneak_internal.utils.network.PostResponse;
import com.usersneak_internal.utils.network.RequestStatus;
import com.usersneak_internal.utils.network.RequestStatus.Status;
import com.usersneak_internal.utils.network.SimpleCallback;

import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

final class UserSneakRepoImpl implements UserSneakRepo {

  private final HashMap<String, RequestStatusLiveData<Integer>> surveyCountMap = new HashMap<>();

  private final RequestStatusLiveData<Boolean> apiEnabled = new RequestStatusLiveData<>();
  private final RequestStatusLiveData<Boolean> resurveyWindowExpired =
      new RequestStatusLiveData<>();
  private final HashMap<String, RequestStatusLiveData<Optional<Survey>>> surveys = new HashMap<>();

  private SurveyResultsHandler handler = null;

  @Override
  public void setApiKey(String apiKey) {
    UserSneakConfigCache.get().storeApiKey(apiKey);
  }

  @Override
  public String getApiKey() {
    return UserSneakConfigCache.get().getApiKey();
  }

  @Override
  public void setSheetId(String id) {
    UserSneakConfigCache.get().storeSheetId(id);
  }

  @Override
  public String getSheetId() {
    return UserSneakConfigCache.get().getSheetId();
  }

  @Override
  public void setResurveyWindow(long millis) {
    UserSneakConfigCache.get().setResurveyWindow(millis);
  }

  @Override
  public void setCustomerId(String id) {
    UserSneakConfigCache.get().setCustomerId(id);
  }

  @Override
  public void setSurveyResultHandler(SurveyResultsHandler handler) {
    this.handler = handler;
  }

  @Override
  public void recordSurveyResults(Survey survey, ImmutableMap<String, String> questionAnswerMap) {
    UserSneakConfigCache.get().recordSurveyTimestamp(System.currentTimeMillis());
    UserSneakServiceGenerator.get().postSurveyResults(
        getApiKey(),
        new PostSurveyResultBody(
            survey.surveyName,
            getSheetId(),
            UserSneakConfigCache.get().getUserId(),
            UserSneakConfigCache.get().getCustomerId()))
        .enqueue(new Callback<PostResponse>() {
          @Override
          public void onResponse(
              @NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
            if (!response.isSuccessful()) {
              scheduleRetry();
            }
          }

          @Override
          public void onFailure(
              @NonNull Call<PostResponse> call, @NonNull Throwable throwable) {
            scheduleRetry();
          }

          private void scheduleRetry() {
            // TODO(allen): cache the response and attempt to upload it later
          }
        });

    if (handler == null) {
      Log.e("UserSneak", "Survey results dropped on the ground D:");
      return;
    }

    handler.handleSurveyResults(
        new SurveyResults(
            survey.surveyName,
            survey.questions.stream().map(q -> (UserSneakQuestion) q).collect(Collectors.toList()),
            questionAnswerMap));
  }

  @Override
  public LiveData<RequestStatus<Boolean>> apiEnabled() {
    if (apiEnabled.getValue().status == Status.INITIAL) {
      refreshApiEnabled();
    }
    return apiEnabled;
  }

  private void refreshApiEnabled() {
    // TODO(allen): talk to our backend to see if the user's key is actually enabled
    String apiKey = UserSneakConfigCache.get().getApiKey();
    apiEnabled.setValue(RequestStatus.success(!Strings.isNullOrEmpty(apiKey)));
  }

  @Override
  public LiveData<RequestStatus<Boolean>> resurveyWindowExpired() {
    if (resurveyWindowExpired.getValue().status == Status.INITIAL) {
      refreshResurveyWindow();
    }
    return resurveyWindowExpired;
  }

  private void refreshResurveyWindow() {
    long resurveyWindow = UserSneakConfigCache.get().getResurveyWindow();
    long now = System.currentTimeMillis();

    // TODO(allen): if lastSurvey == -1, ask the server
    long lastSurvey = UserSneakConfigCache.get().lastSurveyTimeMillis();
    resurveyWindowExpired.setValue(RequestStatus.success(now - resurveyWindow > lastSurvey));
  }

  @Override
  public LiveData<RequestStatus<Integer>> getSurveyResponseCount(String eventName) {
    if (!surveyCountMap.containsKey(eventName)) {
      initSurveyCount(eventName);
    }
    return surveyCountMap.get(eventName);
  }

  @Override
  public LiveData<RequestStatus<Optional<Survey>>> getSurvey(String event) {
    preWarmSurvey(event);
    return surveys.get(event);
  }

  @Override
  public void preWarmSurvey(String event) {
    if (!surveys.containsKey(event)) {
      surveys.put(event, new RequestStatusLiveData<>());
    }
    RequestStatusLiveData<Optional<Survey>> livedata = requireNonNull(surveys.get(event));
    if (livedata.getValue().status == Status.PENDING
        || livedata.getValue().status == Status.SUCCESS) {
      return;
    }

    livedata.setValue(RequestStatus.pending());
    UserSneakServiceGenerator.get()
        .getSurvey(getApiKey(), getSheetId(), event)
        .enqueue(new SimpleCallback<GetSurveyResponse>(livedata) {
          @Override
          public void onResponse(
              @NonNull Call<GetSurveyResponse> call,
              @NonNull Response<GetSurveyResponse> response) {
            if (!response.isSuccessful()
                || response.body() == null
                || !Strings.isNullOrEmpty(response.body().errorMessage)) {
              String message = response.body() == null ? "unknown" : response.body().errorMessage;
              livedata.setValue(
                  RequestStatus.error(
                      new RemoteException("Failed to fetch " + event + " survey: " + message)));
              return;
            }
            Survey survey = Survey.from(response.body());
            switch (survey.status) {
              case NO_SURVEY:
                livedata.setValue(RequestStatus.success(Optional.absent()));
              case AVAILABLE:
                livedata.setValue(RequestStatus.success(Optional.of(survey)));
                break;
              case SURVEY_MALFORMED:
                livedata.setValue(RequestStatus.error(new IllegalArgumentException()));
                break;
            }
          }
        });
  }

  private void initSurveyCount(String eventName) {
    RequestStatusLiveData<Integer> count = new RequestStatusLiveData<>();
    surveyCountMap.put(eventName, count);

    // TODO(allen): Ask our server how many surveys have been recorded
    count.setValue(RequestStatus.success(0));
  }
}
