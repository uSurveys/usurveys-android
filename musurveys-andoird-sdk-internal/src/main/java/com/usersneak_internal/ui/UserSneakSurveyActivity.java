package com.usersneak_internal.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.common.collect.ImmutableMap;
import com.usersneak_internal.R;
import com.usersneak_internal.remote.usersneak.repo.UserSneakModule;
import com.usersneak_internal.ui.SurveyHostFragment.SurveyHostParent;
import com.usersneak_internal.utils.uiutils.FragmentUtils.FragmentUtilListener;

/**
 * Root activity presenting the survey as a bottom sheet.
 */
public final class UserSneakSurveyActivity extends AppCompatActivity
    implements FragmentUtilListener {

  private MainSurveyHostParent surveyHostParent;

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
    surveyHostParent = new MainSurveyHostParent(bottomSheetBehavior);

    findViewById(R.id.bottomsheet_dim).setOnClickListener(view -> {
      // Attempt to log the results we have so far
      SurveyHostFragment fragment = (SurveyHostFragment) getSupportFragmentManager().findFragmentByTag("SURVEY");
      if (fragment != null) {
        fragment.submitSurveyResults();
      }
      surveyHostParent.dismissSurvey();
    });

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
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {
          }
        });
  }

  private static final class MainSurveyHostParent implements SurveyHostParent {

    private final BottomSheetBehavior<View> bottomSheetBehavior;

    private MainSurveyHostParent(BottomSheetBehavior<View> bottomSheetBehavior) {
      this.bottomSheetBehavior = bottomSheetBehavior;
    }

    @Override
    public void dismissSurvey() {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void reportHeight(int height) {
      bottomSheetBehavior.setPeekHeight(height, true);
    }
  }

  @Nullable
  @Override
  public <T> T getImpl(Class<T> callbackInterface) {
    if (callbackInterface.isInstance(surveyHostParent)) {
      return (T) surveyHostParent;
    }
    return null;
  }
}
