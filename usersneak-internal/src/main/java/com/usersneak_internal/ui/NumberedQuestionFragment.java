package com.usersneak_internal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.fragment.app.Fragment;
import com.google.common.collect.ImmutableMap;
import com.usersneak_internal.R;
import com.usersneak_internal.models.QuestionInternal;
import com.usersneak_internal.remote.sheets.repo.SheetsModule;
import com.usersneak_internal.utils.uiutils.FragmentUtils;
import java.util.List;

public final class NumberedQuestionFragment extends Fragment {

  private final ImmutableMap<Integer, Integer> indexToIdMap =
      ImmutableMap.<Integer, Integer>builder()
          .put(0, R.id.one)
          .put(1, R.id.two)
          .put(2, R.id.three)
          .put(3, R.id.four)
          .put(4, R.id.five)
          .put(5, R.id.six)
          .put(6, R.id.seven)
          .put(7, R.id.eight)
          .put(8, R.id.nine)
          .put(9, R.id.ten)
          .build();

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_numbered_question, container, false);
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
        SheetsModule.getInstance().getSurvey(event).getValue().getResult().get().questions.stream()
            .filter(q -> q.getId().equals(questionId))
            .findFirst()
            .get();
    ((TextView) root.findViewById(R.id.question_text)).setText(question.getQuestion());
    for (int i = 0; i < indexToIdMap.size(); i++) {
      setupButton(root, i, question.getAnswers());
    }

    Flow flow = root.findViewById(R.id.answer_flow);
    switch (question.getAnswers().size()) {
      case 7:
      case 8:
        flow.setMaxElementsWrap(4);
        break;
      case 6:
        flow.setMaxElementsWrap(6);
        break;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 9:
      case 10:
      default:
        flow.setMaxElementsWrap(5);
        break;
    }
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
