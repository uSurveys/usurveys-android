package com.usersneak_internal.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.common.base.Strings;
import com.usersneak_api.UserSneakQuestion.Type;
import com.usersneak_internal.R;
import com.usersneak_internal.models.QuestionInternal;
import com.usersneak_internal.models.Survey;
import com.usersneak_internal.remote.sheets.repo.SheetsModule;

public final class SurveyHostFragment extends Fragment {

  public static final String EVENT_NAME_KEY = "event_name_key";

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_survey_host, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    String eventName = requireArguments().getString(EVENT_NAME_KEY, "");
    if (Strings.isNullOrEmpty(eventName)) {
      Log.e("UserSneak", "Event name missing");
      requireActivity().finish();
      return;
    }

    SheetsModule.getInstance()
        .getSurvey(eventName)
        .observe(
            getViewLifecycleOwner(),
            survey -> {
              switch (survey.status) {
                case INITIAL:
                case PENDING:
                  // TODO(allen): Show loading
                  break;
                case SUCCESS:
                  if (!survey.getResult().isPresent()
                      || survey.getResult().get().questions.isEmpty()) {
                    Log.e("UserSneak", "Survey missing. Something went wrong");
                    requireActivity().finish();
                    return;
                  }
                  setupSurvey(survey.getResult().get());
                  break;
                case FAILED:
                  Log.e("UserSneak", "Something went wrong");
                  requireActivity().finish();
                  break;
              }
            });
  }

  int currentQuestion = 0;

  private void setupSurvey(Survey survey) {
    Log.d("UserSneak", "Starting survey");
    setQuestion(survey, currentQuestion);
  }

  private static Class<? extends Fragment> getFragment(Type type) {
    switch (type) {
      case NUMBERED:
        return NumberedQuestionFragment.class;
      case MULTIPLE_CHOICE:
        return MultipleChoiceQuestionFragment.class;
      case LONG_ANSWER:
        return LongAnswerQuestionFragment.class;
      case SHORT_ANSWER:
        return ShortAnswerQuestionFragment.class;
    }
    throw new IllegalArgumentException("Unhandled type: " + type);
  }

  public void nextQuestion() {
    currentQuestion++;
    String eventName = requireArguments().getString(EVENT_NAME_KEY, "");
    setQuestion(
        SheetsModule.getInstance().getSurvey(eventName).getValue().getResult().get(),
        currentQuestion);
  }

  private void setQuestion(Survey survey, int questionIndex) {
    if (questionIndex >= survey.questions.size()) {
      ((UserSneakSurveyActivity) requireActivity()).dismissSurvey();
      return;
    }
    QuestionInternal question = survey.questions.get(questionIndex);
    Bundle args = new Bundle();
    args.putString(EVENT_NAME_KEY, survey.surveyName);
    args.putString("question_id", question.getId());
    getChildFragmentManager()
        .beginTransaction()
        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        .replace(R.id.survey_fragment_root, getFragment(question.getType()), args)
        .commitNow();
  }
}
