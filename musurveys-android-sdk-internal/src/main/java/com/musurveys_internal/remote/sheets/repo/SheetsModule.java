package com.musurveys_internal.remote.sheets.repo;

import androidx.annotation.VisibleForTesting;

public final class SheetsModule {

  private static SheetsRepo sheetsRepo = null;

  public static SheetsRepo getInstance() {
    if (sheetsRepo == null) {
      sheetsRepo = new SheetsRepoImpl();
    }
    return sheetsRepo;
  }

  private SheetsModule() {}

  @VisibleForTesting
  public static void clearForTesting() {
    sheetsRepo = null;
  }
}
