package com.usersneak_internal.remote.usersneak.repo;

import androidx.annotation.VisibleForTesting;

public final class UserSneakModule {

  private static UserSneakRepo instance;

  public static UserSneakRepo getInstance() {
    if (instance == null) {
      instance = new UserSneakRepoImpl();
    }
    return instance;
  }

  @VisibleForTesting
  public static void clearForTesting() {
    instance = null;
  }
}
