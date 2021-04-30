package com.usersneak.usersneak_testing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Utility to observe {@link LiveData} in tests. */
public final class LiveDataTestUtil {

  public static <T> T getOrAwaitValue(LiveData<T> liveData) throws InterruptedException {
    return getOrAwaitValue(liveData, 1);
  }

  public static <T> T getOrAwaitValue(LiveData<T> liveData, int count) throws InterruptedException {
    Object[] data = new Object[1];
    CountDownLatch latch = new CountDownLatch(count);
    Observer<T> observer =
        o -> {
          data[0] = o;
          latch.countDown();
        };
    liveData.observeForever(observer);
    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(2, TimeUnit.SECONDS)) {
      throw new RuntimeException("LiveData value was never set.");
    }
    liveData.removeObserver(observer);
    return (T) data[0];
  }

  private LiveDataTestUtil() {}
}
