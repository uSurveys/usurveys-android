package com.musurveys_api;

import android.app.Application;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import java.util.List;

/**
 * MuSurveys interface.
 *
 * <p>Setup instructions:
 *
 * <ol>
 *   <li>Call {@link #configureSheetsId} from {@link Application#onCreate()}.
 *   <li>Call {@link #configureResurveyWindowMillis} from {@link Application#onCreate()}.
 *   <li>Call {@link #configureSurveyResultsHandler} where convenient.
 * </ol>
 *
 * <p>To show surveys:
 *
 * <ol>
 *   <li>Call {@link #track} to check for surveys for the given event.
 *   <li>Consider calling {@link #preTrack} if you expect that {@link #track} will be called soon.
 *   <li>Call {@link #showSurvey} when {@link StatusCallback#handleStatus} has an {@link
 *       StatusCallback.SurveyStatus#AVAILABLE} status.
 * </ol>
 */
public interface MuSurveysApi {

  /** Configure MuSurveys API key. */
  MuSurveysApi configureMuSurveysApiKey(String apiKey);

  /**
   * Configure MuSurveys to read from GoogleSheets.
   *
   * @param sheetId Google Sheet ID. See docs.musurveys.com/sheets
   */
  MuSurveysApi configureSheetsId(String sheetId);

  /** Configure the amount of milliseconds MuSurveys should wait before resurveying the user. */
  MuSurveysApi configureResurveyWindowMillis(long millis);

  /** Optional: Configure callback to handle survey results. See <link/> for an example. */
  MuSurveysApi configureSurveyResultsHandler(SurveyResultsHandler handler);

  /** Optional: Attach your own unique identifier the each customer's survey responses. */
  MuSurveysApi configureCustomerId(String id);

  /**
   * Ask MuSurveys to preemptively track an event and prep a survey.
   *
   * <p>This reduces load times when {@link #track} is called.
   */
  void preTrack(String event);

  /** Ask MuSurveys if there is a survey available for the given {@code event}. */
  void track(String event, StatusCallback statusCallback);

  /**
   * Ask MuSurveys to show the last {@link StatusCallback.SurveyStatus#AVAILABLE available} survey.
   */
  void showSurvey(FragmentActivity activity, String event, ActivityResultLauncher<Intent> launcher);

  /** Clear any locally stored settings. */
  void logout();

  /**
   * Ask MuSurveys for list of all available events.
   *
   * This is usually used for testing configurations.
   */
  void getAllEvents(AllEventsCallback callback);

  interface StatusCallback {

    enum SurveyStatus {
      /** No survey is available at this time. */
      NO_SURVEY,

      /** Survey is available. Call {@link #showSurvey}. */
      AVAILABLE,

      /** Survey is malformed. Check logs for reason. */
      SURVEY_MALFORMED,
    }

    /** Called when the survey status is available. */
    @MainThread
    void handleStatus(@NonNull SurveyStatus status);
  }

  interface AllEventsCallback {

    void handleEventsReady(@NonNull List<String> events);
  }
}
