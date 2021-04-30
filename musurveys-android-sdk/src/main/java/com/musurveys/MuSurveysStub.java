package com.musurveys;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;
import com.musurveys_api.SurveyResultsHandler;
import com.musurveys_api.MuSurveysApi;
import java.util.ArrayList;

public final class MuSurveysStub implements MuSurveysApi {

  @Override
  public MuSurveysApi configureMuSurveysApiKey(String apiKey) {
    return this;
  }

  @Override
  public MuSurveysApi configureSheetsId(String sheetId) {
    return this;
  }

  @Override
  public MuSurveysApi configureSurveyResultsHandler(SurveyResultsHandler handler) {
    return this;
  }

  @Override
  public MuSurveysApi configureCustomerId(String id) {
    return this;
  }

  @Override
  public MuSurveysApi configureResurveyWindowMillis(long millis) {
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
    // TODO(MuSurveys): figure out a way to short circuit the launcher here.
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
