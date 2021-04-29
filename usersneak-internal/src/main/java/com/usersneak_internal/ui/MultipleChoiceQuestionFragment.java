package com.usersneak_internal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.common.collect.ImmutableMap;
import com.usersneak_internal.R;
import com.usersneak_internal.models.QuestionInternal;
import com.usersneak_internal.remote.sheets.repo.SheetsModule;
import com.usersneak_internal.remote.usersneak.repo.UserSneakModule;
import com.usersneak_internal.utils.uiutils.FragmentUtils;
import java.util.List;

public final class MultipleChoiceQuestionFragment extends Fragment {

  private final ImmutableMap<Integer, Integer> indexToIdMap =
      ImmutableMap.<Integer, Integer>builder()
          .put(0, R.id.mc_1)
          .put(1, R.id.mc_2)
          .put(2, R.id.mc_3)
          .put(3, R.id.mc_4)
          .put(4, R.id.mc_5)
          .build();

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_multiple_choice_question, container, false);
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

    String event = requireArguments().getString(SurveyHostFragment.EVENT_NAME_KEY);
    String questionId = requireArguments().getString(SurveyQuestionParent.QUESTION_ID_KEY);
    QuestionInternal question =
        UserSneakModule.getInstance().getSurvey(event).getValue().getResult().get().questions.stream()
            .filter(q -> q.getId().equals(questionId))
            .findFirst()
            .get();
    ((TextView) root.findViewById(R.id.question_text)).setText(question.getQuestion());

    setupButton(root, 0, question.getAnswers());
    setupButton(root, 1, question.getAnswers());
    setupButton(root, 2, question.getAnswers());
    setupButton(root, 3, question.getAnswers());
    setupButton(root, 4, question.getAnswers());
  }

  private void setupButton(View root, int index, List<String> answers) {
    TextView button = root.findViewById(indexToIdMap.get(index));
    if (answers.size() >= index + 1) {
      button.setVisibility(View.VISIBLE);
      button.setText(answers.get(index));
      button.setOnClickListener(view -> getParent().submitAnswer(answers.get(index)));
    } else {
      button.setVisibility(View.GONE);
    }
  }

  private SurveyQuestionParent getParent() {
    return FragmentUtils.getParentUnsafe(this, SurveyQuestionParent.class);
  }
}
