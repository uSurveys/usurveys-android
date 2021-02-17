package com.usersneak_internal.remote.usersneak.repo;

import android.util.Log;
import androidx.lifecycle.LiveData;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.usersneak_api.SurveyResultsHandler;
import com.usersneak_internal.models.Answer;
import com.usersneak_internal.models.QuestionInternal;
import com.usersneak_internal.models.Survey;
import com.usersneak_internal.remote.usersneak.cache.UserSneakConfigCache;
import com.usersneak_internal.utils.RequestStatusLiveData;
import com.usersneak_internal.utils.network.RequestStatus;
import com.usersneak_internal.utils.network.RequestStatus.Status;
import java.util.HashMap;

final class UserSneakRepoImpl implements UserSneakRepo {

  private final HashMap<String, RequestStatusLiveData<Integer>> surveyCountMap = new HashMap<>();

  private final RequestStatusLiveData<Boolean> apiEnabled = new RequestStatusLiveData<>();
  private final RequestStatusLiveData<Boolean> resurveyWindowExpired =
      new RequestStatusLiveData<>();

  private SurveyResultsHandler handler = null;

  @Override
  public void setApiKey(String apiKey) {
    UserSneakConfigCache.get().storeApiKey(apiKey);
  }

  @Override
  public String getApiKey() {
    return UserSneakConfigCache.get().getApiKey();
  }

  @Override
  public void setSheetId(String id) {
    UserSneakConfigCache.get().storeSheetId(id);
  }

  @Override
  public String getSheetId() {
    return UserSneakConfigCache.get().getSheetId();
  }

  @Override
  public void setResurveyWindow(long millis) {
    UserSneakConfigCache.get().setResurveyWindow(millis);
  }

  @Override
  public void setSurveyResultHandler(SurveyResultsHandler handler) {
    this.handler = handler;
  }

  @Override
  public void recordSurveyResults(Survey survey, ImmutableMap<QuestionInternal, Answer> answers) {
    if (handler != null) {
      // TODO(allen): send the survey results
      handler.handleSurveyResults(null);
    } else {
      Log.e("UserSneak", "Survey results dropped on the ground D:");
    }

    UserSneakConfigCache.get().recordSurveyTimestamp(System.currentTimeMillis());
    // TODO(allen): upload the survey results to our server?
    // TODO(allen): notify our survey that a survey was taken (to increment count)
  }

  @Override
  public LiveData<RequestStatus<Boolean>> apiEnabled() {
    if (apiEnabled.getValue().status == Status.INITIAL) {
      refreshApiEnabled();
    }
    return apiEnabled;
  }

  private void refreshApiEnabled() {
    // TODO(allen): talk to our backend to see if the user's key is actually enabled
    String apiKey = UserSneakConfigCache.get().getApiKey();
    apiEnabled.setValue(RequestStatus.success(!Strings.isNullOrEmpty(apiKey)));
  }

  @Override
  public LiveData<RequestStatus<Boolean>> resurveyWindowExpired() {
    if (resurveyWindowExpired.getValue().status == Status.INITIAL) {
      refreshResurveyWindow();
    }
    return resurveyWindowExpired;
  }

  private void refreshResurveyWindow() {
    long resurveyWindow = UserSneakConfigCache.get().getResurveyWindow();
    long now = System.currentTimeMillis();

    // TODO(allen): if lastSurvey == -1, ask the server
    long lastSurvey = UserSneakConfigCache.get().lastSurveyTimeMillis();
    resurveyWindowExpired.setValue(RequestStatus.success(now - resurveyWindow > lastSurvey));
  }

  @Override
  public LiveData<RequestStatus<Integer>> getSurveyResponseCount(String eventName) {
    if (!surveyCountMap.containsKey(eventName)) {
      initSurveyCount(eventName);
    }
    return surveyCountMap.get(eventName);
  }

  private void initSurveyCount(String eventName) {
    RequestStatusLiveData<Integer> count = new RequestStatusLiveData<>();
    surveyCountMap.put(eventName, count);

    // TODO(allen): Ask our server how many surveys have been recorded
    count.setValue(RequestStatus.success(0));
  }
}
