package com.usersneak_api;

import android.app.Application;
import android.content.Context;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import java.util.List;

/**
 * UserSneak interface.
 *
 * <p>Setup instructions:
 *
 * <ol>
 *   <li>Call {@link #configureSheetsApi} from {@link Application#onCreate()}.
 *   <li>Call {@link #configureResurveyWindowMillis} from {@link Application#onCreate()}.
 *   <li>Call {@link #configureSurveyResultsHandler} where convenient.
 *   <li>Call {@link #showTestSurvey} to validate that everything is setup properly.
 * </ol>
 *
 * To show surveys:
 *
 * <ol>
 *   <li>Call {@link #track} to check for surveys for the given event.
 *   <li>Consider calling {@link #preTrack} if you expect that {@link #track} will be called soon.
 *   <li>Call {@link #showSurvey} when {@link StatusCallback#handleStatus} has an {@link
 *       StatusCallback.SurveyStatus#AVAILABLE} status.
 * </ol>
 */
public interface UserSneakApi {

  /** Configure UserSneak API key. */
  UserSneakApi configureUserSneakApiKey(Context context, String apiKey);

  /**
   * Configure UserSneak to read from GoogleSheets.
   *
   * @param sheetsApiKey Google Sheets API key. See TODO(allen): link to documentation.
   * @param sheetId Google Sheet ID. See TODO(allen): link to documentation.
   */
  UserSneakApi configureSheetsApi(Context context, String sheetsApiKey, String sheetId);

  /** Configure callback to handle survey results. See <link/> for an example. */
  UserSneakApi configureSurveyResultsHandler(Context context, SurveyResultsHandler handler);

  /** Configure the amount of milliseconds UserSneak should wait before resurveying the user. */
  UserSneakApi configureResurveyWindowMillis(long millis);

  /**
   * Ask UserLeap to preemptively track an event and prep a survey.
   *
   * <p>This reduces load times when {@link #track} is called.
   */
  void preTrack(String event);

  /** Ask UserSneak if there is a survey available for the given {@code event}. */
  void track(String event, StatusCallback statusCallback);

  /**
   * Ask UserSneak to show the last {@link StatusCallback.SurveyStatus#AVAILABLE available} survey.
   */
  void showSurvey(FragmentActivity activity, ActivityResultCallback<ActivityResult> resultCallback);

  /** Clear any locally stored settings. */
  void logout(boolean clearResurveyWindow);

  /** Ask UserSneak to show a test survey. */
  void showTestSurvey(
      FragmentActivity activity, ActivityResultCallback<ActivityResult> resultCallback);

  /**
   * Ask UserSneak for list of all available events. This is usually used for testing
   * configurations.
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
