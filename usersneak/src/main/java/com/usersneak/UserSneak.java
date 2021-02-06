package com.usersneak;

import com.usersneak_api.UserSneakApi;
import com.usersneak_internal.UserSneakImpl;

/** @see UserSneakApi for setup/usage instructions. */
public final class UserSneak {

  public static final UserSneakApi INSTANCE = new UserSneakImpl();
}
