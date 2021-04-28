package com.usersneak_internal.remote.usersneak.api.models;

public class PostSurveyResultBody {

  public final String eventName;
  public final String sheetId;
  public final String userId;
  public final String customerId;

  public PostSurveyResultBody(String eventName, String sheetId, String userId, String customerId) {
    this.eventName = eventName;
    this.sheetId = sheetId;
    this.userId = userId;
    this.customerId = customerId;
  }
}
