package com.musurveys_internal.remote.sheets.repo;

import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.musurveys_internal.models.Survey;
import com.musurveys_internal.remote.sheets.api.SheetsServiceGenerator;
import com.musurveys_internal.remote.sheets.api.responses.GetSheetResponse;
import com.musurveys_internal.remote.sheets.api.responses.GetSheetResponse.ServerSheet;
import com.musurveys_internal.remote.sheets.api.responses.SheetsValuesResponse;
import com.musurveys_internal.remote.musurveys.repo.MuSurveysModule;
import com.musurveys_internal.remote.musurveys.repo.MuSurveysRepo;
import com.musurveys_internal.utils.RequestStatusLiveData;
import com.musurveys_internal.utils.network.RequestStatus;
import com.musurveys_internal.utils.network.RequestStatus.Status;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final class SheetsRepoImpl implements SheetsRepo {

  private static final String SHEETS_API_KEY = "<your-sheets-api-key-here>";
  private static final String DEBUG_SHEET_ID = "1CSF6Vyxi31x0IeceG4h41OmfM0qcnL9aa3tV3TvYwE8";

  private final MuSurveysRepo muSurveysRepo = MuSurveysModule.getInstance();
  private final RequestStatusLiveData<ImmutableList<String>> surveyTitles =
      new RequestStatusLiveData<>();
  private final HashMap<String, RequestStatusLiveData<Optional<Survey>>> surveys = new HashMap<>();

  @Override
  public void preWarmEventNames() {
    if (surveyTitles.getValue().status == Status.PENDING) {
      return;
    }

    surveyTitles.setValue(RequestStatus.pending());
    SheetsServiceGenerator.get()
        .getSheets(muSurveysRepo.getSheetId(), SHEETS_API_KEY)
        .enqueue(
            new Callback<GetSheetResponse>() {
              @Override
              public void onResponse(
                  @NonNull Call<GetSheetResponse> call,
                  @NonNull Response<GetSheetResponse> response) {
                if (!response.isSuccessful()) {
                  Log.e("MuSurveys", "GetSheets failed with non-200 error");
                  surveyTitles.setValue(
                      RequestStatus.error(
                          new RemoteException("Failed to fetch sheet: " + response.code())));
                  return;
                }

                if (response.body() == null || response.body().sheets.isEmpty()) {
                  Log.e("MuSurveys", "GetSheets failed: Sheet has no sheets");
                  surveyTitles.setValue(
                      RequestStatus.error(new RemoteException("Failed to fetch sheet: no sheets")));
                  return;
                }

                for (ServerSheet sheet : response.body().sheets) {
                  if (Strings.isNullOrEmpty(sheet.properties.title)) {
                    Log.e("MuSurveys", "GetSheets failed: Sheet has no ID");
                    continue;
                  }
                  // titleToIdMap.put(sheet.properties.title, sheet.properties.sheetId);
                  if (!surveys.containsKey(sheet.properties.title)) {
                    // Check that the key is not already there (like when user requests an event
                    // before
                    // pre-warming is done.
                    surveys.put(sheet.properties.title, new RequestStatusLiveData<>());
                  }
                }

                surveyTitles.setValue(
                    RequestStatus.success(ImmutableList.copyOf(surveys.keySet())));

                // For all the survey fetches that were paused because of pre-warming, resume
                // fetching
                Set<Entry<String, RequestStatusLiveData<Optional<Survey>>>> entries =
                    surveys.entrySet();
                for (Entry<String, RequestStatusLiveData<Optional<Survey>>> survey : entries) {
                  if (survey.getValue().getValue().status == Status.PENDING) {
                    resumeSurveyInit(survey.getKey(), survey.getValue());
                  }
                }
              }

              @Override
              public void onFailure(
                  @NonNull Call<GetSheetResponse> call, @NonNull Throwable throwable) {
                Log.e("MuSurveys", "GetSheets failed for unknown reason");
                surveyTitles.setValue(
                    RequestStatus.error(new Exception("Failed to fetch sheets", throwable)));
              }
            });
  }

  @Override
  public LiveData<RequestStatus<ImmutableList<String>>> getEventNames() {
    if (surveyTitles.getValue().status == Status.INITIAL) {
      preWarmEventNames();
    }
    return surveyTitles;
  }

  @Override
  public void preWarmSurvey(String event) {
    if (!surveys.containsKey(event)) {
      surveys.put(event, new RequestStatusLiveData<>());
    }
    initSurvey(event, Objects.requireNonNull(surveys.get(event)));
  }

  @Override
  public LiveData<RequestStatus<Optional<Survey>>> getSurvey(String event) {
    preWarmSurvey(event);
    return surveys.get(event);
  }

  private void initSurvey(String event, RequestStatusLiveData<Optional<Survey>> liveData) {
    RequestStatus<Optional<Survey>> status = liveData.getValue();
    if (status.status == Status.PENDING || status.status == Status.SUCCESS) {
      return;
    }

    if (surveyTitles.getValue().status != Status.SUCCESS) {
      liveData.setValue(RequestStatus.pending());
      preWarmEventNames();
      return;
    }

    if (Strings.isNullOrEmpty(event)) {
      liveData.setValue(RequestStatus.success(Optional.absent()));
      return;
    }

    liveData.setValue(RequestStatus.pending());
    resumeSurveyInit(event, liveData);
  }

  private void resumeSurveyInit(String event, RequestStatusLiveData<Optional<Survey>> liveData) {
    SheetsServiceGenerator.get()
        .getValues(muSurveysRepo.getSheetId(), String.format("'%s'!A1:Z50", event), SHEETS_API_KEY)
        .enqueue(
            new Callback<SheetsValuesResponse>() {
              @Override
              public void onResponse(
                  @NonNull Call<SheetsValuesResponse> call,
                  @NonNull Response<SheetsValuesResponse> response) {
                if (!response.isSuccessful()) {
                  Log.e("MuSurveys", "GetSheets failed: Sheet has no sheets");
                  liveData.setValue(
                      RequestStatus.error(new RemoteException("Failed to fetch sheet: ")));
                  return;
                }

                if (response.body() == null
                    || response.body().values == null
                    || response.body().values.isEmpty()) {
                  Log.e("MuSurveys", "GetSheet failed: Sheet is missing or empty");
                  liveData.setValue(
                      RequestStatus.error(
                          new RemoteException("Failed to fetch sheet: Sheet is missing or empty")));
                  return;
                }

                SheetsValuesResponse body = SheetsValuesResponse.clean(response.body());
                Survey survey = Survey.from(body, event);
                switch (survey.status) {
                  case NO_SURVEY:
                    liveData.setValue(RequestStatus.success(Optional.absent()));
                    break;

                  case AVAILABLE:
                    liveData.setValue(RequestStatus.success(Optional.of(survey)));
                    break;

                  case SURVEY_MALFORMED:
                    liveData.setValue(
                        RequestStatus.error(new RemoteException("Survey malformed: " + event)));
                    break;

                  default:
                    throw new IllegalArgumentException("Unhandled survey status: " + survey.status);
                }
              }

              @Override
              public void onFailure(
                  @NonNull Call<SheetsValuesResponse> call, @NonNull Throwable throwable) {
                Log.e("MuSurveys", "GetSheetValues failed for unknown reason");
                liveData.setValue(
                    RequestStatus.error(new Exception("Failed to fetch sheet values", throwable)));
              }
            });
  }
}
