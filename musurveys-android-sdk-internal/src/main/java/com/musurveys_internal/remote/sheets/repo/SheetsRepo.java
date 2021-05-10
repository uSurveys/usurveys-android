package com.musurveys_internal.remote.sheets.repo;

import androidx.lifecycle.LiveData;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.musurveys_internal.models.Survey;
import com.musurveys_internal.utils.network.RequestStatus;

public interface SheetsRepo {

  void preWarmEventNames();

  LiveData<RequestStatus<ImmutableList<String>>> getEventNames();

}
