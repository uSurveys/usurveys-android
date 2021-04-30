package com.musurveys_internal.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.musurveys_api.MuSurveysQuestion.Type;
import com.musurveys_internal.R;
import com.musurveys_internal.models.QuestionInternal;
import com.musurveys_internal.models.Survey;
import com.musurveys_internal.remote.musurveys.repo.MuSurveysModule;
import com.musurveys_internal.utils.uiutils.FragmentUtils;
import com.musurveys_internal.utils.uiutils.FragmentUtils.FragmentUtilListener;
import java.util.HashMap;

public final class SurveyHostFragment extends Fragment implements FragmentUtilListener {

  public static final String EVENT_NAME_KEY = "event_name_key";

  private SurveyQuestionsParent surveyQuestionsParent;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_survey_host, container, false);
    view.setOnTouchListener((v, event) -> true);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    String eventName = requireArguments().getString(EVENT_NAME_KEY, "");
    if (Strings.isNullOrEmpty(eventName)) {
      Log.e("MuSurveys", "Event name missing");
      requireActivity().finish();
      return;
    }

    MuSurveysModule.getInstance()
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
                    Log.e("MuSurveys", "Survey missing. Something went wrong");
                    requireActivity().finish();
                    return;
                  }
                  surveyQuestionsParent =
                      new SurveyQuestionsParent(
                          FragmentUtils.getParentUnsafe(this, SurveyHostParent.class),
                          getChildFragmentManager(),
                          survey.getResult().get());
                  break;
                case FAILED:
                  Log.e("MuSurveys", "Something went wrong");
                  requireActivity().finish();
                  break;
              }
            });
  }

  @Nullable
  @Override
  public <T> T getImpl(Class<T> callbackInterface) {
    if (callbackInterface.isInstance(surveyQuestionsParent)) {
      return (T) surveyQuestionsParent;
    }
    return null;
  }

  public void submitSurveyResults() {
    surveyQuestionsParent.submitSurveyResults();
  }

  private static class SurveyQuestionsParent implements SurveyQuestionParent {

    private final SurveyHostParent surveyHostParent;
    private final FragmentManager childFragmentManager;
    private final Survey survey;

    private final HashMap<String, String> questionAnswerMap = new HashMap<>();

    private String currentQuestionId = "";

    public SurveyQuestionsParent(
        SurveyHostParent surveyHostParent, FragmentManager childFragmentManager, Survey survey) {
      this.surveyHostParent = surveyHostParent;
      this.childFragmentManager = childFragmentManager;
      this.survey = survey;
      currentQuestionId = survey.questions.get(0).getId();
      setQuestion(survey.questions.get(0));
    }

    @Override
    public void reportHeight(int height) {
      surveyHostParent.reportHeight(height);
    }

    @Override
    public void submitAnswer(String answer) {
      questionAnswerMap.put(currentQuestionId, answer);
      Optional<String> nextQuestion = findQuestions(currentQuestionId).getNextQuestion(answer);
      if (!nextQuestion.isPresent()) {
        submitSurveyResults();
        surveyHostParent.dismissSurvey();
        return;
      }
      setQuestion(findQuestions(nextQuestion.get()));
    }

    private void setQuestion(QuestionInternal question) {
      currentQuestionId = question.getId();
      Bundle args = new Bundle();
      args.putString(EVENT_NAME_KEY, survey.surveyName);
      args.putString(QUESTION_ID_KEY, currentQuestionId);
      childFragmentManager
          .beginTransaction()
          .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
          .replace(R.id.survey_fragment_root, getFragment(question.getType()), args)
          .commitNow();
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

    private QuestionInternal findQuestions(String id) {
      return survey.questions.stream().filter(q -> q.getId().equals(id)).findFirst().get();
    }

    public void submitSurveyResults() {
      MuSurveysModule.getInstance()
          .recordSurveyResults(survey, ImmutableMap.copyOf(questionAnswerMap));
    }
  }

  public interface SurveyHostParent {

    void dismissSurvey();

    void reportHeight(int height);
  }
}
