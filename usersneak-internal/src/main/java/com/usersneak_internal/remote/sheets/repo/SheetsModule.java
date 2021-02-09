package com.usersneak_internal.remote.sheets.repo;

public final class SheetsModule {

  private static SheetsRepo sheetsRepo = null;

  public static SheetsRepo getInstance() {
    if (sheetsRepo == null) {
      sheetsRepo = new SheetsRepoImpl();
    }
    return sheetsRepo;
  }

  private SheetsModule() {}
}
