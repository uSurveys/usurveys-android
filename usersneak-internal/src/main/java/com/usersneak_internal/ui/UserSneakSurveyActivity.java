package com.usersneak_internal.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.usersneak_internal.R;

/** Root activity presenting the survey as a bottom sheet. */
public final class UserSneakSurveyActivity extends AppCompatActivity {

  public static Intent create(Context context, String event) {
    Intent intent = new Intent(context, UserSneakSurveyActivity.class);
    intent.putExtra(SurveyHostFragment.EVENT_NAME_KEY, event);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_sneak_survey);
    initBottomSheetBehavior();
    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.bottom_sheet_root, SurveyHostFragment.class, getIntent().getExtras(), "SURVEY")
        .commitNow();
  }

  private void initBottomSheetBehavior() {
    BottomSheetBehavior<View> bottomSheetBehavior =
        BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_root));

    findViewById(R.id.bottomsheet_dim).setOnClickListener(view -> dismissSurvey());

    // Expanded by default
    bottomSheetBehavior.setDraggable(false);
    bottomSheetBehavior.setPeekHeight(0);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    bottomSheetBehavior.addBottomSheetCallback(
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
              finish();
              // Cancels animation on finish()
              // overridePendingTransition(0, 0);
            }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
  }

  public void reportHeight(int height) {
    BottomSheetBehavior<View> bottomSheetBehavior =
        BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_root));
    bottomSheetBehavior.setPeekHeight(height, true);
  }

  public void dismissSurvey() {
    BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_root))
        .setState(BottomSheetBehavior.STATE_HIDDEN);
  }
}
