package com.usersneak_internal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.usersneak_internal.R;
import com.usersneak_internal.utils.uiutils.FragmentUtils;

public final class ShortAnswerQuestionFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_short_answer_question, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(root, savedInstanceState);
    root.getViewTreeObserver()
        .addOnGlobalLayoutListener(
            new OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                getParent().reportHeight(root.getHeight());
              }
            });

    root.findViewById(R.id.next_question).setOnClickListener(view -> getParent().submitAnswer(""));
  }

  private SurveyQuestionParent getParent() {
    return FragmentUtils.getParentUnsafe(this, SurveyQuestionParent.class);
  }
}
