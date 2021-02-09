package com.usersneak_internal.models;

import java.util.List;

public final class Survey {

  public final String surveyId;
  public final String surveyName;
  public final List<Question> questions;

  public Survey(String surveyId, String surveyName, List<Question> questions) {
    this.surveyId = surveyId;
    this.surveyName = surveyName;
    this.questions = questions;
  }
}
