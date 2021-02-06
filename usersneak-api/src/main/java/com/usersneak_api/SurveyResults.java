package com.usersneak_api;

import java.util.List;

public final class SurveyResults {

  public final String surveyId;
  public final String surveyName;
  public final List<UserSneakQuestion> questions;

  public SurveyResults(String surveyId, String surveyName, List<UserSneakQuestion> questions) {
    this.surveyId = surveyId;
    this.surveyName = surveyName;
    this.questions = questions;
  }
}
