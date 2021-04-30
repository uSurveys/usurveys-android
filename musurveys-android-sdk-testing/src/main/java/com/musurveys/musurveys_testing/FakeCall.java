package com.musurveys.musurveys_testing;

import com.google.common.base.Optional;
import okhttp3.Request;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Utility for mocking retrofit calls and triggering callbacks. */
public class FakeCall<T> implements Call<T> {

  private final Optional<Response<T>> response;
  private final Optional<Throwable> throwable;

  public static <S> FakeCall<S> success(Response<S> response) {
    return new FakeCall<>(Optional.of(response), Optional.absent());
  }

  public static <S> FakeCall<S> error(Throwable throwable) {
    return new FakeCall<>(Optional.absent(), Optional.of(throwable));
  }

  private FakeCall(Optional<Response<T>> response, Optional<Throwable> throwable) {
    this.response = response;
    this.throwable = throwable;
  }

  @NotNull
  @Override
  public Response<T> execute() {
    return response.get();
  }

  @Override
  public void enqueue(@NotNull Callback<T> callback) {
    if (response.isPresent()) {
      callback.onResponse(this, response.get());
    } else if (throwable.isPresent()) {
      callback.onFailure(this, throwable.get());
    } else {
      throw new IllegalStateException("Both response and throwable are missing");
    }
  }

  @Override
  public boolean isExecuted() {
    throw new UnsupportedOperationException("Stub!");
  }

  @Override
  public void cancel() {
    throw new UnsupportedOperationException("Stub!");
  }

  @Override
  public boolean isCanceled() {
    throw new UnsupportedOperationException("Stub!");
  }

  @NotNull
  @Override
  public Call<T> clone() {
    throw new UnsupportedOperationException("Stub!");
  }

  @NotNull
  @Override
  public Request request() {
    throw new UnsupportedOperationException("Stub!");
  }

  @NotNull
  @Override
  public Timeout timeout() {
    throw new UnsupportedOperationException("Stub!");
  }
}
