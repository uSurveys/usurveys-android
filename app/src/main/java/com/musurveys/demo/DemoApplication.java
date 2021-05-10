package com.musurveys.demo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.musurveys.MuSurveys;
import java.util.concurrent.TimeUnit;

public final class DemoApplication extends Application {

  private static final String USER_SNEAK_API_KEY = "07162f6f-4ac1-4a38-8131-94b5cda5b95f";
  private static final String SHEET_ID = "1CSF6Vyxi31x0IeceG4h41OmfM0qcnL9aa3tV3TvYwE8";

  private static DemoApplication application;

  public static Context getContext() {
    return application;
  }

  public static DemoApplication get() {
    return application;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    application = this;
    configureMuSurveys(getApiKey(), getSheetId());
  }

  public void updateKeys(String apiKey, String sheetId) {
    if (Strings.isNullOrEmpty(apiKey) || Strings.isNullOrEmpty(sheetId)) {
      Toast.makeText(this, "API Key or sheet ID missing", Toast.LENGTH_SHORT).show();
      return;
    }

    SharedPreferences prefs = getSharedPreferences("demo_app_keys", MODE_PRIVATE);
    prefs.edit()
        .putString("demo_app_api_key", apiKey)
        .putString("demo_app_sheet_id", sheetId)
        .apply();
    configureMuSurveys(apiKey, sheetId);
  }

  public String getSheetId() {
    SharedPreferences prefs = getSharedPreferences("demo_app_keys", MODE_PRIVATE);
    return prefs.getString("demo_app_sheet_id", SHEET_ID);
  }

  public String getApiKey() {
    SharedPreferences prefs = getSharedPreferences("demo_app_api_key", MODE_PRIVATE);
    return prefs.getString("demo_app_api_key", USER_SNEAK_API_KEY);
  }

  // Sign up at https://musurveys.com, navigate to settings to find your API key.
  private void configureMuSurveys(String apiKey, String sheetId) {
    MuSurveys.get()
        .configureMuSurveysApiKey(apiKey)
        // See https://docs.musurveys.com to learn how to setup your sheet.
        .configureSheetsId(sheetId)
        // Handle survey results to capture with your own logging framework.
        // See https://docs.musurveys.com to learn more.
        .configureSurveyResultsHandler(new DemoSurveyResultsHandler());
  }
}
