package com.usersneak_internal.application;

import android.content.Context;

public final class AppContext {

  private static Context context;

  public static Context get() {
    return context;
  }

  public static void init(Context context) {
    AppContext.context = context;
  }
}
