package com.usersneak;

import android.content.Context;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import com.usersneak_api.SurveyResultsHandler;
import com.usersneak_api.UserSneakApi;
import java.util.ArrayList;

public final class UserSneakStub implements UserSneakApi {

  @Override
  public UserSneakApi configureUserSneakApiKey(Context context, String apiKey) {
    return this;
  }

  @Override
  public UserSneakApi configureSheetsApi(Context context, String sheetId) {
    return this;
  }

  @Override
  public UserSneakApi configureSurveyResultsHandler(Context context, SurveyResultsHandler handler) {
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
      FragmentActivity activity,
      String event,
      ActivityResultCallback<ActivityResult> resultCallback) {
    resultCallback.onActivityResult(new ActivityResult(AppCompatActivity.RESULT_OK, null));
  }

  @Override
  public void logout(boolean clearResurveyWindow) {}

  @Override
  public void getAllEvents(AllEventsCallback callback) {
    ArrayList<String> events = new ArrayList<>();
    events.add("App First Launch");
    events.add("Onboarding Complete");
    events.add("User Achieved Milestone");
    callback.handleEventsReady(events);
  }
}
