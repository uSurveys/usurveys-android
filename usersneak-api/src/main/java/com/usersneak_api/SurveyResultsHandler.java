package com.usersneak_api;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

public interface SurveyResultsHandler {

  /** Called when the user completes a survey. */
  @MainThread
  void handleSurveyResults(@NonNull SurveyResults results);
}
