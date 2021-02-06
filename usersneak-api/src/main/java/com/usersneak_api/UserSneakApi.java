package com.usersneak_api;

import android.app.Application;
import android.content.Context;

import java.time.Duration;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * UserSneak interface.
 *
 * Setup instructions:
 * <ol>
 *   <li>Call {@link #configureSheetsApi(Context, String, String)} from {@link Application#onCreate()}.</li>
 *   <li>Call {@link #configureResurveyWindow(Duration)} from {@link Application#onCreate()}.</li>
 *   <li>Call {@link #configureSurveyResultsHandler(Context, SurveyResultsHandler)} where convenient.</li>
 *   <li>Call {@link #showTestSurvey(AppCompatActivity)} to validate that everything is setup properly.</li>
 * </ol>
 *
 * To show surveys:
 * <ol>
 *   <li>Call {@link #track(String, EventCallback) track} to check for surveys for the given event. </li>
 *   <li>Consider calling {@link #preTrack(String)} if you expect that {@link #track(String, EventCallback)} will be called soon.</li>
 *   <li>Call {@link #showSurvey(AppCompatActivity)} when {@link EventCallback#handleStatus(EventCallback.SurveyStatus)} has an {@link EventCallback.SurveyStatus#AVAILABLE} status.</li>
 * </ol>
 */
public interface UserSneakApi {

  /**
   * Configure UserSneak to read from GoogleSheets.
   *
   * @param sheetsApiKey Google Sheets API key. See TODO(allen): link to documentation.
   * @param sheetId Google Sheet ID. See TODO(allen): link to documentation.
   */
  void configureSheetsApi(Context context, String sheetsApiKey, String sheetId);

  /** Configure callback to handle survey results. See <link/> for an example. */
  void configureSurveyResultsHandler(Context context, SurveyResultsHandler handler);

  /** Configure the amount of time UserSneak should wait before resurveying the user. */
  void configureResurveyWindow(Duration duration);

  /**
   * Ask UserLeap to preemptively track an event and prep a survey.
   *
   * This reduces load times when {@link #track(String, EventCallback)} is called.
   */
  void preTrack(String event);

  /** Ask UserSneak if there is a survey available for the given {@code event}. */
  void track(String event, EventCallback callback);

  /** Ask UserSneak to show the last {@link EventCallback.SurveyStatus#AVAILABLE available} survey. */
  void showSurvey(AppCompatActivity activity);

  /** Ask UserSneak to show a test survey. */
  void showTestSurvey(AppCompatActivity activity);

  /** Clear any locally stored settings. */
  void logout(boolean clearResurveyWindow);

  interface EventCallback {

    enum SurveyStatus {
      /** No survey is available at this time. */
      NO_SURVEY,

      /** Survey is available. Call {@link #showSurvey(AppCompatActivity)}. */
      AVAILABLE,

      /** Survey is malformed. Check logs for reason. */
      SURVEY_MALFORMED,
    }

    /** Called when the survey status is available. */
    @MainThread
    void handleStatus(@NonNull SurveyStatus status);
  }
}
