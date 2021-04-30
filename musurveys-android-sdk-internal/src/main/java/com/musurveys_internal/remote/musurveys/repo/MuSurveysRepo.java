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

  void setResurveyWindow(long millis);

  void setSurveyResultHandler(SurveyResultsHandler handler);

  void recordSurveyResults(Survey survey, ImmutableMap<String, String> questionAnswerMap);

  void setCustomerId(String id);

  /** Returns true if the client's API key is valid and in good standing. */
  LiveData<RequestStatus<Boolean>> apiEnabled();

  /**
   * Returns true if the user is outside the resurvey window (i.e. they're eligible to see another
   * survey).
   */
  LiveData<RequestStatus<Boolean>> resurveyWindowExpired();

  /** Returns the total number of survey responses that have been recorded so far. */
  LiveData<RequestStatus<Integer>> getSurveyResponseCount(String eventName);

  void preWarmSurvey(String event);

  LiveData<RequestStatus<Optional<Survey>>> getSurvey(String event);

  void logout();

  void logError(String stack);
}