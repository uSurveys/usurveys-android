package com.usersneak_internal.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.usersneak_internal.R;

/** Root activity presenting the survey as a bottom sheet. */
public final class UserSneakSurveyActivity extends AppCompatActivity {

  public static Intent create(Context context, String event) {
    Intent intent = new Intent(context, UserSneakSurveyActivity.class);
    intent.putExtra("event_name_key", event);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_sneak_survey);
  }
}
