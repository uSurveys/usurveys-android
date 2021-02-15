package com.usersneak.demo.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.usersneak.UserSneak;
import com.usersneak.demo.R;
import com.usersneak_api.UserSneakApi.StatusCallback;

public final class DetailFragment extends Fragment {

  ActivityResultLauncher<Intent> userSneakLauncher =
      registerForActivityResult(
          new StartActivityForResult(),
          result -> {
            Toast.makeText(requireContext(), "Survey completed", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
          });

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
    String eventName = DetailFragmentArgs.fromBundle(requireArguments()).getEventName();

    Toolbar toolbar = root.findViewById(R.id.toolbar);
    toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root).popBackStack());
    toolbar.setTitle(eventName);

    root.findViewById(R.id.btn_pre_track)
        .setOnClickListener(view -> UserSneak.get().preTrack(eventName));

    root.findViewById(R.id.btn_track)
        .setOnClickListener(
            view -> {
              StatusCallback callback =
                  status -> {
                    switch (status) {
                      case NO_SURVEY:
                        Toast.makeText(requireContext(), "No survey", Toast.LENGTH_SHORT).show();
                        break;

                      case AVAILABLE:
                        UserSneak.get().showSurvey(requireActivity(), eventName, userSneakLauncher);
                        break;

                      case SURVEY_MALFORMED:
                        Toast.makeText(requireContext(), "Malformed", Toast.LENGTH_SHORT).show();
                        break;
                    }
                  };

              UserSneak.get().track(eventName, callback);
            });
  }
}
