package com.musurveys.demo.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.musurveys.demo.DemoApplication;
import com.musurveys.demo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public final class SetupFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_setup, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(root, savedInstanceState);
    EditText apiKey = ((EditText) root.findViewById(R.id.et_api_key));
    apiKey.setText(DemoApplication.get().getApiKey());

    EditText sheetId = ((EditText) root.findViewById(R.id.et_sheet_id));
    sheetId.setText(DemoApplication.get().getSheetId());

    root.findViewById(R.id.btn_clear).setOnClickListener(view -> {
      apiKey.setText("");
      sheetId.setText("");
    });

    root.findViewById(R.id.btn_save_creds)
        .setOnClickListener(
            view -> {
              DemoApplication.get().updateKeys(
                  apiKey.getText().toString(), sheetId.getText().toString());
              Navigation.findNavController(requireView()).popBackStack();
            }
        );

    root.findViewById(R.id.tv_documentation)
        .setOnClickListener(view -> openBrowser("https://docs.musurveys.com"));
    root.findViewById(R.id.tv_create_account)
        .setOnClickListener(view -> openBrowser("https://musurveys.com/signup"));
  }

  private void openBrowser(String url) {
    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
    startActivity(browserIntent);
  }
}
