package com.usersneak_internal.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.usersneak_internal.utils.network.RequestStatus;
import java.util.Objects;

public final class RequestStatusLiveData<T> extends MutableLiveData<RequestStatus<T>> {

  public RequestStatusLiveData() {
    super(RequestStatus.initial());
  }

  @Override
  public void setValue(@NonNull RequestStatus<T> value) {
    super.setValue(Objects.requireNonNull(value));
  }

  @NonNull
  @Override
  public RequestStatus<T> getValue() {
    return Objects.requireNonNull(super.getValue());
  }
}
