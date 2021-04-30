package com.musurveys_internal.models;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.musurveys_api.MuSurveysApi.StatusCallback.SurveyStatus;
import com.musurveys_api.MuSurveysQuestion;
import com.musurveys_internal.models.Answer.Logic;
import com.musurveys_internal.remote.sheets.api.responses.SheetsValuesResponse;
import com.musurveys_internal.remote.musurveys.api.models.GetSurveyResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class Survey {

  public final String surveyName;
  public final SurveyStatus status;
  public final ImmutableList<QuestionInternal> questions;

  public Survey(String surveyName, SurveyStatus status, List<QuestionInternal> questions) {
    this.surveyName = surveyName;
    this.status = status;
    this.questions = ImmutableList.copyOf(questions);
  }

  public static Survey from(SheetsValuesResponse response, String surveyName) {
    return null;
  }

  public static Survey from(GetSurveyResponse response) {
    return new Survey(
        response.survey.surveyName,
        SurveyStatus.valueOf(response.survey.status),
        response.survey.questions.stream()
            .map(Survey::convert)
            .collect(toList()));
  }

  private static QuestionInternal convert(GetSurveyResponse.ServerQuestion serverQuestion) {
    MuSurveysQuestion.Type type = MuSurveysQuestion.Type.valueOf(serverQuestion.type);
    switch (type) {
      case NUMBERED:
        return new MultipleChoiceQuestion(
            serverQuestion.id,
            serverQuestion.question,
            MultipleChoiceQuestion.Type.NUMBERED,
            serverQuestion.answers.stream()
                .map(a -> new McAnswer(a.text, Logic.valueOf(a.logic), a.nextQuestion))
                .collect(toList()));
      case MULTIPLE_CHOICE:
        return new MultipleChoiceQuestion(
            serverQuestion.id,
            serverQuestion.question,
            MultipleChoiceQuestion.Type.MC,
            serverQuestion.answers.stream()
                .map(a -> new McAnswer(a.text, Logic.valueOf(a.logic), a.nextQuestion))
                .collect(toList()));
      case LONG_ANSWER:
      case SHORT_ANSWER:
        Log.i("###", serverQuestion.question);
        String nextQuestion =
            Strings.nullToEmpty(Strings.emptyToNull(serverQuestion.openEndedAnswer.nextQuestion));
        OpenEndedAnswer answer;
        if (nextQuestion == null) {
          answer = new OpenEndedAnswer(Logic.valueOf(serverQuestion.openEndedAnswer.logic));
        } else {
          answer =
              new OpenEndedAnswer(
                  Logic.valueOf(serverQuestion.openEndedAnswer.logic),
                  nextQuestion);
        }
        return new OpenEndedQuestion(
            serverQuestion.id,
            serverQuestion.question,
            OpenEndedQuestion.Type.from(serverQuestion.type),
            answer);
    }
    return null;
  }
}
