package com.usersneak;

import android.content.Context;

import com.usersneak_api.SurveyResultsHandler;
import com.usersneak_api.UserSneakApi;
import com.usersneak_internal.UserSneakImpl;

import java.time.Duration;

import androidx.appcompat.app.AppCompatActivity;

/** @see UserSneakApi for setup/usage instructions. */
public final class UserSneak implements UserSneakApi {

  public static final UserSneakImpl INSTANCE = new UserSneakImpl();

  @Override
  public void configureSheetsApi(Context context, String sheetsApiKey, String sheetId) {
    INSTANCE.configureSheetsApi(context, sheetsApiKey, sheetId);
  }

  @Override
  public void configureSurveyResultsHandler(Context context, SurveyResultsHandler handler) {
    INSTANCE.configureSurveyResultsHandler(context, handler);
  }

  @Override
  public void configureResurveyWindow(Duration duration) {
    INSTANCE.configureResurveyWindow(duration);
  }

  @Override
  public void preTrack(String event) {
    INSTANCE.preTrack(event);
  }

  @Override
  public void track(String event, EventCallback callback) {
    INSTANCE.track(event, callback);
  }

  @Override
  public void showSurvey(AppCompatActivity activity) {
    INSTANCE.showSurvey(activity);
  }

  @Override
  public void showTestSurvey(AppCompatActivity activity) {
    INSTANCE.showTestSurvey(activity);
  }

  @Override
  public void logout(boolean clearResurveyWindow) {
    INSTANCE.logout(clearResurveyWindow);
  }
}
