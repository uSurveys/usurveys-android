package com.musurveys.demo;

import android.app.Application;
import android.content.Context;
import com.musurveys.MuSurveys;
import java.util.concurrent.TimeUnit;

public final class DemoApplication extends Application {

  private static final String USER_SNEAK_API_KEY = "07162f6f-4ac1-4a38-8131-94b5cda5b95f";
  private static final String SHEET_ID = "1CSF6Vyxi31x0IeceG4h41OmfM0qcnL9aa3tV3TvYwE8";

  private static DemoApplication application;

  public static Context getContext() {
    return application;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    application = this;

    // Sign up at musurveys.com, navigate to settings to find your API key.
    MuSurveys.get()
        .configureMuSurveysApiKey(USER_SNEAK_API_KEY)
        // See docs.musurveys.com to learn how to setup your sheet.
        .configureSheetsId(SHEET_ID)
        // Set the minimum amount of time that must pass before the user is shown another survey.
        // See docs.musurveys.com to learn more.
        .configureResurveyWindowMillis(TimeUnit.SECONDS.toMillis(5))
        // Handle survey results to capture with your own logging framework.
        // See docs.musurveys.com to learn more.
        .configureSurveyResultsHandler(new DemoSurveyResultsHandler());
  }
}
