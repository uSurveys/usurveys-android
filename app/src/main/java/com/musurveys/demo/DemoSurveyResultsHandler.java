package com.musurveys.demo;

import android.util.Log;
import androidx.annotation.NonNull;
import com.musurveys_api.SurveyResults;
import com.musurveys_api.SurveyResultsHandler;

public final class DemoSurveyResultsHandler implements SurveyResultsHandler {

  @Override
  public void handleSurveyResults(@NonNull SurveyResults results) {
    // TODO: Handle survey results and send to your server!
    for (int i = 0; i < results.questions.size(); i++) {
      Log.i(
          "MuSurveys Demo", results.questions.get(i).getQuestion() + " " + results.answers.get(i));
    }
  }
}
