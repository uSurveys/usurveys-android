package com.musurveys;

import androidx.annotation.VisibleForTesting;
import com.musurveys_api.MuSurveysApi;
import com.musurveys_internal.MuSurveysImpl;

/** @see MuSurveysApi for setup/usage instructions. */
public final class MuSurveys {

  private static MuSurveysApi INSTANCE = new MuSurveysImpl();

  public static MuSurveysApi get() {
    return INSTANCE;
  }

  @VisibleForTesting
  public static void setForTesting(MuSurveysApi muSurveysApi) {
    INSTANCE = muSurveysApi;
  }

  @VisibleForTesting
  public static void setStubInstance() {
    INSTANCE = new MuSurveysStub();
  }
}
