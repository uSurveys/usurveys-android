package com.musurveys_internal.utils.common;

import android.util.Log;

import com.google.common.base.Supplier;
import com.musurveys_internal.remote.musurveys.repo.MuSurveysModule;

public final class NeverCrashUtil {

  public static <T> boolean safeCall(Supplier<T> supplier) {
    try {
      supplier.get();
    } catch (Exception e) {
      try {
        String stack = stackTrace(e.getStackTrace());
        Log.e("MuSurveysCrash", stack);
        MuSurveysModule.getInstance().logError(stack);
      } catch (Exception ignored) {}
      return true;
    }
    return false;
  }

  private static String stackTrace(StackTraceElement[] stackTrace) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1 || i < stackTrace.length; i++) {
      sb.append(stackTrace[i].toString()).append("\n");
    }
    return sb.toString();
  }

  private NeverCrashUtil() {}
}
