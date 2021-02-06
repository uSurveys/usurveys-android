package com.usersneak.demo.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.usersneak.UserSneak;
import com.usersneak.demo.R;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public final class DetailFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_detail, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(root, savedInstanceState);
    String eventName = "foo";

    Toolbar toolbar = root.findViewById(R.id.toolbar);
    toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root).popBackStack());
    toolbar.setTitle(eventName);

    root.findViewById(R.id.btn_pre_track).setOnClickListener(view -> UserSneak.INSTANCE.preTrack(eventName));
    root.findViewById(R.id.btn_track).setOnClickListener(
        view -> UserSneak.INSTANCE.track(
            eventName,
            status -> {
              switch (status) {
                case NO_SURVEY:
                  Toast.makeText(requireContext(), "No survey", Toast.LENGTH_SHORT).show();
                  break;
                case AVAILABLE:
                  Toast.makeText(requireContext(), "Showing survey", Toast.LENGTH_SHORT).show();
                  UserSneak.INSTANCE.showSurvey(requireActivity(), result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                      Navigation.findNavController(root).popBackStack();
                    } else if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED) {
                      Toast.makeText(requireContext(), "Survey cancelled", Toast.LENGTH_SHORT).show();
                      Navigation.findNavController(root).popBackStack();
                    }
                  });
                  break;
                case SURVEY_MALFORMED:
                  break;
              }
            }));
  }
}
