package com.usersneak.demo.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.usersneak.demo.EventNameAdapter;
import com.usersneak.demo.R;

public final class MainFragment extends Fragment {

  public static MainFragment newInstance() {
    return new MainFragment();
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.main_fragment, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(root, savedInstanceState);
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
  }
}
