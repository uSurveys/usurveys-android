package com.usersneak_api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SurveyResults {

  public final String surveyName;
  public final List<UserSneakQuestion> questions;
  public final List<String> answers;

  public SurveyResults(
      String surveyName,
      List<UserSneakQuestion> questions,
      Map<String, String> questionIdAnswerMap) {
    this.surveyName = surveyName;
    this.questions = questions;
    answers = new ArrayList<>();
    for (UserSneakQuestion question : questions) {
      if (questionIdAnswerMap.containsKey(question.getId())) {
        answers.add(questionIdAnswerMap.get(question.getId()));
      } else {
        answers.add("");
      }
    }
  }
}
