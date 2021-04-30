package com.usersneak_internal.remote.sheets.repo;

import androidx.lifecycle.LiveData;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.usersneak_internal.models.Survey;
import com.usersneak_internal.utils.network.RequestStatus;

public interface SheetsRepo {

  void preWarmEventNames();

  LiveData<RequestStatus<ImmutableList<String>>> getEventNames();

  void preWarmSurvey(String event);

  LiveData<RequestStatus<Optional<Survey>>> getSurvey(String event);
}
