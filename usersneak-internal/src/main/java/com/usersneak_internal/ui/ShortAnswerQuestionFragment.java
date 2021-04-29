package com.usersneak_internal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.usersneak_internal.R;
import com.usersneak_internal.models.QuestionInternal;
import com.usersneak_internal.remote.sheets.repo.SheetsModule;
import com.usersneak_internal.remote.usersneak.repo.UserSneakModule;
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

    String event = requireArguments().getString(SurveyHostFragment.EVENT_NAME_KEY);
    String questionId = requireArguments().getString(SurveyQuestionParent.QUESTION_ID_KEY);
    QuestionInternal question =
        UserSneakModule.getInstance().getSurvey(event).getValue().getResult().get().questions.stream()
            .filter(q -> q.getId().equals(questionId))
            .findFirst()
            .get();
    ((TextView) root.findViewById(R.id.question_text)).setText(question.getQuestion());

    Button submit = root.findViewById(R.id.next_question);
    EditText input = root.findViewById(R.id.answer_box);
    submit.setOnClickListener(view -> getParent().submitAnswer(input.getText().toString().trim()));
  }

  private SurveyQuestionParent getParent() {
    return FragmentUtils.getParentUnsafe(this, SurveyQuestionParent.class);
  }
}
