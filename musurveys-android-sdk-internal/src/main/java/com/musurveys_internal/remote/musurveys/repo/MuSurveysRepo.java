package com.musurveys_internal.remote.musurveys.repo;

import androidx.lifecycle.LiveData;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.musurveys_api.SurveyResultsHandler;
import com.musurveys_internal.models.Survey;
import com.musurveys_internal.utils.network.RequestStatus;

public interface MuSurveysRepo {

  void setApiKey(String apiKey);

  String getApiKey();

  void setSheetId(String id);

  String getSheetId();

  void setSurveyResultHandler(SurveyResultsHandler handler);

  void recordSurveyResults(Survey survey, ImmutableMap<String, String> questionAnswerMap);

  void setCustomerId(String id);

  void preWarmSurvey(String event);

  LiveData<RequestStatus<Optional<Survey>>> getSurvey(String event);

  void logout();

  void logError(String stack);
}
