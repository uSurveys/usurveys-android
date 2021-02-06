package com.usersneak_internal;

import android.content.Context;

import com.usersneak_api.SurveyResultsHandler;
import com.usersneak_api.UserSneakApi;

import java.time.Duration;

import androidx.appcompat.app.AppCompatActivity;

public final class UserSneakImpl implements UserSneakApi {

  @Override
  public void configureSheetsApi(Context context, String sheetsApiKey, String sheetId) {

  }

  @Override
  public void configureSurveyResultsHandler(Context context, SurveyResultsHandler handler) {

  }

  @Override
  public void configureResurveyWindow(Duration duration) {

  }

  @Override
  public void preTrack(String event) {

  }

  @Override
  public void track(String event, EventCallback callback) {

  }

  @Override
  public void showSurvey(AppCompatActivity activity) {

  }

  @Override
  public void showTestSurvey(AppCompatActivity activity) {

  }

  @Override
  public void logout(boolean clearResurveyWindow) {

  }
}
