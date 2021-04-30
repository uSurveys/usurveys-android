package com.usersneak_internal.remote.usersneak.api.models;

import java.util.List;

public class PostSurveyResultBody {

  public final String eventName;
  public final String sheetId;
  public final String userId;
  public final String customerId;
  public final List<SurveyResponse> surveyResponses;

  public PostSurveyResultBody(
      String eventName,
      String sheetId,
      String userId,
      String customerId,
      List<SurveyResponse> surveyResponses) {
    this.eventName = eventName;
    this.sheetId = sheetId;
    this.userId = userId;
    this.customerId = customerId;
    this.surveyResponses = surveyResponses;
  }

  public static class SurveyResponse {
    public final String questionId;
    public final String answer;

    public SurveyResponse(String questionId, String answer) {
      this.questionId = questionId;
      this.answer = answer;
    }
  }
}
