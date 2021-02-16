package com.usersneak_internal.ui;

public interface SurveyQuestionParent {

  /** Called when the survey questions height changes. */
  void reportHeight(int height);

  /** Called when the user selects/inputs an answer. */
  void submitAnswer(String answer);
}
