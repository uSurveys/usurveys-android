package com.usersneak_internal.remote.usersneak.repo;

import androidx.lifecycle.LiveData;
import com.usersneak_internal.utils.network.RequestStatus;

public interface UserSneakRepo {

  LiveData<RequestStatus<Boolean>> apiEnabled();
}
