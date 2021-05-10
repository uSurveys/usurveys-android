package com.musurveys_internal.remote.sheets.repo;

import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.musurveys_internal.BuildConfig;
import com.musurveys_internal.models.Survey;
import com.musurveys_internal.remote.sheets.api.SheetsServiceGenerator;
import com.musurveys_internal.remote.sheets.api.responses.GetSheetResponse;
import com.musurveys_internal.remote.sheets.api.responses.GetSheetResponse.ServerSheet;
import com.musurveys_internal.remote.sheets.api.responses.SheetsValuesResponse;
import com.musurveys_internal.remote.musurveys.repo.MuSurveysModule;
import com.musurveys_internal.remote.musurveys.repo.MuSurveysRepo;
import com.musurveys_internal.utils.RequestStatusLiveData;
import com.musurveys_internal.utils.common.Assert;
import com.musurveys_internal.utils.network.RequestStatus;
import com.musurveys_internal.utils.network.RequestStatus.Status;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final class SheetsRepoImpl implements SheetsRepo {

  private final MuSurveysRepo muSurveysRepo = MuSurveysModule.getInstance();
  private final RequestStatusLiveData<ImmutableList<String>> surveyTitles =
      new RequestStatusLiveData<>();

  @Override
  public void preWarmEventNames() {
    if (surveyTitles.getValue().status == Status.PENDING) {
      return;
    }

    surveyTitles.setValue(RequestStatus.pending());
    SheetsServiceGenerator.get()
        .getSheets(muSurveysRepo.getSheetId(), Objects.requireNonNull(BuildConfig.SHEETS_API_KEY))
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

                List<String> titles = response.body().sheets.stream()
                    .map(sheet -> sheet.properties.title)
                    .filter(title -> !Strings.isNullOrEmpty(title))
                    .collect(Collectors.toList());
                surveyTitles.setValue(
                    RequestStatus.success(ImmutableList.copyOf(titles)));
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
}
