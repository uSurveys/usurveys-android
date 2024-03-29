package com.musurveys.demo.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.musurveys.MuSurveys;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MainViewModel extends ViewModel {

  private final MutableLiveData<List<String>> events = new MutableLiveData<>(new ArrayList<>());

  public LiveData<List<String>> getEventNames() {
    if (Objects.requireNonNull(events.getValue()).isEmpty()) {
      MuSurveys.get().getAllEvents(events::setValue);
    }
    return events;
  }
}
