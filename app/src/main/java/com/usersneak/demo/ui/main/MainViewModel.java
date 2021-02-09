package com.usersneak.demo.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.usersneak.UserSneak;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainViewModel extends ViewModel {

  private final MutableLiveData<List<String>> events = new MutableLiveData<>(new ArrayList<>());

  public LiveData<List<String>> getEventNames() {
    if (Objects.requireNonNull(events.getValue()).isEmpty()) {
      UserSneak.get().getAllEvents(events::setValue);
    }
    return events;
  }
}
