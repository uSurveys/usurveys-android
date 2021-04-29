package com.usersneak;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;
import com.usersneak_api.SurveyResultsHandler;
import com.usersneak_api.UserSneakApi;
import java.util.ArrayList;

public final class UserSneakStub implements UserSneakApi {

  @Override
  public UserSneakApi configureUserSneakApiKey(String apiKey) {
    return this;
  }

  @Override
  public UserSneakApi configureSheetsId(String sheetId) {
    return this;
  }

  @Override
  public UserSneakApi configureSurveyResultsHandler(SurveyResultsHandler handler) {
    return this;
  }

  @Override
  public UserSneakApi configureCustomerId(String id) {
    return this;
  }

  @Override
  public UserSneakApi configureResurveyWindowMillis(long millis) {
    return this;
  }

  @Override
  public void preTrack(String event) {}

  @Override
  public void track(String event, StatusCallback statusCallback) {
    statusCallback.handleStatus(StatusCallback.SurveyStatus.AVAILABLE);
  }

  @Override
  public void showSurvey(
      FragmentActivity activity, String event, ActivityResultLauncher<Intent> launcher) {
    // TODO(usersneak): figure out a way to short circuit the launcher here.
  }

  @Override
  public void logout() {}

  @Override
  public void getAllEvents(AllEventsCallback callback) {
    ArrayList<String> events = new ArrayList<>();
    events.add("App First Launch");
    events.add("Onboarding Complete");
    events.add("User Achieved Milestone");
    callback.handleEventsReady(events);
  }
}
