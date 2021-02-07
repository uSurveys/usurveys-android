package com.usersneak.demo;

import android.app.Application;
import android.content.Context;
import com.usersneak.UserSneak;
import java.util.concurrent.TimeUnit;

public final class DemoApplication extends Application {

  private static final String USER_SNEAK_API_KEY = "user_sneak_api_key";
  private static final String SHEETS_API_KEY = "sheets_api_key";
  private static final String SHEET_ID = "sheet_id";

  private static DemoApplication application;

  public static Context getContext() {
    return application;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    application = this;

    // Sign up at usersneak.com, navigate to settings to find your API key.
    UserSneak.INSTANCE
        .configureUserSneakApiKey(this, USER_SNEAK_API_KEY)
        // See docs.usersneak.com/sheets to learn how to setup your sheet.
        .configureSheetsApi(this, SHEETS_API_KEY, SHEET_ID)
        // Set the minimum amount of time that must pass before the user is shown another survey.
        // See docs.userneak.com/resurvey to learn more.
        .configureResurveyWindowMillis(TimeUnit.SECONDS.toMillis(5))
        // Handle survey results to capture with your own logging framework.
        // See docs.usersneak.com/logging?os=android to learn more.
        .configureSurveyResultsHandler(this, new DemoSurveyResultsHandler());
  }
}
