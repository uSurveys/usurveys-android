package com.usersneak_api;

import java.util.List;

public final class SurveyResults {

  public final String surveyName;
  public final List<UserSneakQuestion> questions;

  public SurveyResults(String surveyName, List<UserSneakQuestion> questions) {
    this.surveyName = surveyName;
    this.questions = questions;
  }
}
