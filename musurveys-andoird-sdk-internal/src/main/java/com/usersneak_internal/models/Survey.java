package com.usersneak_internal.models;

import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.usersneak_api.UserSneakApi.StatusCallback.SurveyStatus;
import com.usersneak_api.UserSneakQuestion;
import com.usersneak_internal.models.Answer.Logic;
import com.usersneak_internal.models.MultipleChoiceQuestion.Type;
import com.usersneak_internal.remote.sheets.api.responses.SheetsValuesResponse;
import com.usersneak_internal.remote.usersneak.api.models.GetSurveyResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    UserSneakQuestion.Type type = UserSneakQuestion.Type.valueOf(serverQuestion.type);
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
