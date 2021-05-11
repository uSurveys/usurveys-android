package com.musurveys.demo.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.musurveys.MuSurveys;
import com.musurveys.demo.EventNameAdapter;
import com.musurveys.demo.R;
import com.musurveys_api.MuSurveysApi;

public final class MainFragment extends Fragment {

  ActivityResultLauncher<Intent> muSurveysLauncher =
      registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(root, savedInstanceState);
    root.findViewById(R.id.btn_settings).setOnClickListener(view ->
        Navigation.findNavController(root)
            .navigate(MainFragmentDirections.actionMainFragmentToSetupFragment()));
    RecyclerView recyclerView = root.findViewById(R.id.rv_events);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
    EventNameAdapter adapter =
        new EventNameAdapter(
            event ->
                Navigation.findNavController(root)
                    .navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(event)));
    recyclerView.setAdapter(adapter);

    MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    viewModel.getEventNames().observe(getViewLifecycleOwner(), adapter::setEvents);

    String eventName = "Uber NPS";
    MuSurveys.get().logout();
    MuSurveysApi.StatusCallback callback =
        status -> {
          switch (status) {
            case NO_SURVEY:
              Toast.makeText(requireContext(), "No survey", Toast.LENGTH_SHORT).show();
              break;

            case AVAILABLE:
              MuSurveys.get().showSurvey(requireActivity(), eventName, muSurveysLauncher);
              break;

            case SURVEY_MALFORMED:
              Toast.makeText(requireContext(), "Malformed", Toast.LENGTH_SHORT).show();
              break;
          }
        };

    MuSurveys.get().track(eventName, callback);
  }
}
