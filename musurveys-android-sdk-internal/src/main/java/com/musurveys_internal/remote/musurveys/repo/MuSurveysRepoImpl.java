package com.musurveys_internal.remote.musurveys.repo;

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
import com.musurveys_api.SurveyResults;
import com.musurveys_api.SurveyResultsHandler;
import com.musurveys_api.MuSurveysQuestion;
import com.musurveys_internal.BuildConfig;
import com.musurveys_internal.models.Survey;
import com.musurveys_internal.remote.musurveys.api.MuSurveysServiceGenerator;
import com.musurveys_internal.remote.musurveys.api.models.GetSurveyResponse;
import com.musurveys_internal.remote.musurveys.api.models.PostSurveyResultBody;
import com.musurveys_internal.remote.musurveys.api.models.PostSurveyResultBody.SurveyResponse;
import com.musurveys_internal.remote.musurveys.cache.MuSurveysConfigCache;
import com.musurveys_internal.remote.sheets.repo.SheetsModule;
import com.musurveys_internal.utils.RequestStatusLiveData;
import com.musurveys_internal.utils.common.NeverCrashUtil;
import com.musurveys_internal.utils.network.PostResponse;
import com.musurveys_internal.utils.network.RequestStatus;
import com.musurveys_internal.utils.network.RequestStatus.Status;
import com.musurveys_internal.utils.network.SimpleCallback;

import java.util.HashMap;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

final class MuSurveysRepoImpl implements MuSurveysRepo {

  private final HashMap<String, RequestStatusLiveData<Optional<Survey>>> surveys = new HashMap<>();

  private SurveyResultsHandler handler = results -> {};

  @Override
  public void setApiKey(String apiKey) {
    MuSurveysConfigCache.get().storeApiKey(apiKey);
  }

  @Override
  public String getApiKey() {
    return MuSurveysConfigCache.get().getApiKey();
  }

  @Override
  public void setSheetId(String id) {
    MuSurveysConfigCache.get().storeSheetId(id);
    if (BuildConfig.DEBUG) {
      SheetsModule.getInstance().preWarmEventNames();
    }
  }

  @Override
  public String getSheetId() {
    return MuSurveysConfigCache.get().getSheetId();
  }

  @Override
  public void setCustomerId(String id) {
    MuSurveysConfigCache.get().setCustomerId(id);
  }

  @Override
  public void setSurveyResultHandler(SurveyResultsHandler handler) {
    this.handler = handler;
  }

  @Override
  public void recordSurveyResults(Survey survey, ImmutableMap<String, String> questionAnswerMap) {
    MuSurveysConfigCache.get().recordSurveyTimestamp(System.currentTimeMillis());
    surveys.get(survey.surveyName).setValue(RequestStatus.success(Optional.absent()));
    MuSurveysServiceGenerator.get().postSurveyResults(
        getApiKey(),
        new PostSurveyResultBody(
            survey.surveyName,
            getSheetId(),
            MuSurveysConfigCache.get().getUserId(),
            MuSurveysConfigCache.get().getCustomerId(),
            survey.questions.stream()
                .map(q ->
                    new SurveyResponse(q.getId(), questionAnswerMap.getOrDefault(q.getId(), "")))
                .collect(toList())))
        .enqueue(new Callback<PostResponse>() {
          @Override
          public void onResponse(
              @NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
            NeverCrashUtil.safeCall(() -> {
              if (!response.isSuccessful()) {
                scheduleRetry();
              }
              return null;
            });
          }

          @Override
          public void onFailure(
              @NonNull Call<PostResponse> call, @NonNull Throwable throwable) {
            NeverCrashUtil.safeCall(() -> {
              scheduleRetry();
              return null;
            });
          }

          private void scheduleRetry() {
            // TODO(allen): cache the response and attempt to upload it later
          }
        });

    handler.handleSurveyResults(
        new SurveyResults(
            survey.surveyName,
            survey.questions.stream().map(q -> (MuSurveysQuestion) q).collect(toList()),
            questionAnswerMap));
  }

  @Override
  public LiveData<RequestStatus<Optional<Survey>>> getSurvey(String event) {
    preWarmSurvey(event);
    return surveys.get(event);
  }

  @Override
  public void logout() {
    MuSurveysConfigCache.get().logout();
    surveys.clear();
  }

  @Override
  public void logError(String stack) {

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
    MuSurveysServiceGenerator.get()
        .getSurvey(getApiKey(), getSheetId(), event, MuSurveysConfigCache.get().getUserId())
        .enqueue(new SimpleCallback<GetSurveyResponse>(livedata) {
          @Override
          public void onSafeResponse(
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
}
