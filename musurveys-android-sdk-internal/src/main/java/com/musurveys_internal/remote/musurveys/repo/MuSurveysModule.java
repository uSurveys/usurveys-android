package com.musurveys_internal.remote.musurveys.repo;

import androidx.annotation.VisibleForTesting;

public final class MuSurveysModule {

  private static MuSurveysRepo instance;

  public static MuSurveysRepo getInstance() {
    if (instance == null) {
      instance = new MuSurveysRepoImpl();
    }
    return instance;
  }

  @VisibleForTesting
  public static void clearForTesting() {
    instance = null;
  }
}
