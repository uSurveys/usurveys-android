package com.usersneak;

import androidx.annotation.VisibleForTesting;
import com.usersneak_api.UserSneakApi;
import com.usersneak_internal.UserSneakImpl;

/** @see UserSneakApi for setup/usage instructions. */
public final class UserSneak {

  private static UserSneakApi INSTANCE = new UserSneakImpl();

  public static UserSneakApi get() {
    return INSTANCE;
  }

  @VisibleForTesting
  public static void setForTesting(UserSneakApi userSneak) {
    INSTANCE = userSneak;
  }

  @VisibleForTesting
  public static void setStubInstance() {
    INSTANCE = new UserSneakStub();
  }
}
