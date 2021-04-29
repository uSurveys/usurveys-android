package com.usersneak_internal.utils.network;


import com.usersneak_internal.utils.RequestStatusLiveData;

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
  public abstract void onResponse(@NonNull Call<T> call, @NonNull Response<T> response);

  @Override
  public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
    livedata.setValue(RequestStatus.error(throwable));
  }
}
