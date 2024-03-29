package com.musurveys.demo.analytics;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Pair;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

public final class AppAnalyticsWorker extends Worker {

  private static final String TAG = "AppAnalyticsWorker";
  private static final String ANALYTICS_EVENT_NAME_KEY = "analytics_event_name";
  private static final String ANALYTICS_EVENT_TIMESTAMP_KEY = "analytics_event_timestamp";

  private static final String NO_MS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /**
   * Schedule an analytics event to be logged.
   *
   * <p>Uses {@link WorkManager} to schedule a job using {@link #TAG} to label them and append them
   * to a queue using {@link ExistingWorkPolicy#APPEND}.
   *
   * <p>Tasks are executed:
   *
   * <ul>
   *   <li>In the order they're added (FIFO)
   *   <li>On a background thread managed by the OS
   *   <li>When the device is connected to the internet
   *   <li>Retried when there are failures with an exponential backoff
   * </ul>
   */
  public static Pair<OneTimeWorkRequest, Operation> track(
      Context context, String name, HashMap<String, Object> properties) {
    Constraints constraints =
        new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

    for (Entry<String, Object> entry : properties.entrySet()) {
      if (entry.getValue() instanceof List) {
        throw new IllegalStateException("Use arrays instead of lists");
      }
    }

    SimpleDateFormat sdf = new SimpleDateFormat(NO_MS_DATE_FORMAT, Locale.getDefault());
    String date = sdf.format(new Date());

    Data inputData =
        new Data.Builder()
            .putAll(properties)
            .putString(ANALYTICS_EVENT_NAME_KEY, name)
            .putString(ANALYTICS_EVENT_TIMESTAMP_KEY, date)
            .build();

    OneTimeWorkRequest request =
        new OneTimeWorkRequest.Builder(AppAnalyticsWorker.class)
            .setConstraints(constraints)
            .addTag(TAG)
            .setInputData(inputData)
            .build();
    Operation operation =
        WorkManager.getInstance(context).enqueueUniqueWork(TAG, ExistingWorkPolicy.APPEND, request);
    return Pair.create(request, operation);
  }

  public AppAnalyticsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    return logEvent();
  }

  @WorkerThread
  private Result logEvent() {
    // Parse input
    HashMap<String, Object> properties = new HashMap<>(getInputData().getKeyValueMap());
    String eventName = (String) properties.remove(ANALYTICS_EVENT_NAME_KEY);
    String timestamp = (String) properties.remove(ANALYTICS_EVENT_TIMESTAMP_KEY);

    // TODO: build a call to your analytics endpoint here.
    // Call<Void> call = null; // = backend.postAnalyticsEvent(body);

    // Execute the request in this thread
    // Response<Void> response;
    // try {
    //   response = call.execute();
    // } catch (IOException e) {
    //   Log.e(TAG, "Flushing analytics event failed", e);
    //   return Result.retry();
    // }

    // If the request failed, try again after a backoff
    // if (response.code() != 200 && response.code() != 201) {
    //   Log.e(TAG, "Flushing analytics event request failed");
    //   return Result.retry();
    // }

    // Event successfully logged
    Log.d(TAG, "Successfully flushed event. Response code: " /* + response.code() */);
    return Result.success();
  }
}
