package com.musurveys_internal.utils.network;


import com.musurveys_internal.utils.RequestStatusLiveData;
import com.musurveys_internal.utils.common.NeverCrashUtil;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SimpleCallback<T> implements Callback<T> {

  private final RequestStatusLiveData<?> livedata;

  protected SimpleCallback(RequestStatusLiveData<?> livedata) {
    this.livedata = livedata;
  }

  @Override
  public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
    boolean crashed = NeverCrashUtil.safeCall(() -> {
      onSafeResponse(call, response);
      return null;
    });
    if (crashed) {
      livedata.setValue(RequestStatus.error(new Exception("MuSurveys crashed.")));
    }
  }

  public abstract void onSafeResponse(@NonNull Call<T> call, @NonNull Response<T> response);

  @Override
  public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
    livedata.setValue(RequestStatus.error(throwable));
  }
}
