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
    List<String> status = null;
    List<String> headers = null;
    ArrayList<List<String>> sheetsQuestions = new ArrayList<>();

    for (List<String> row : response.values) {
      if (headers != null) {
        sheetsQuestions.add(row);
        continue;
      }

      String rowId = row.get(0);
      if (SheetsValuesResponse.STATUS.equals(rowId)) {
        status = row;
      } else if (SheetsValuesResponse.LABEL_ID.equals(rowId)) {
        headers = row;
      } else {
        Log.d("UserSneak", "Row not supported: " + rowId);
      }
    }

    // Parse & Validate headers
    String headersError = headerError(headers);
    if (!Strings.isNullOrEmpty(headersError)) {
      return createMalformed(headersError, surveyName);
    }

    // Parse & Validate Questions
    int idIndex = headers.indexOf(SheetsValuesResponse.LABEL_ID);
    int questionIndex = headers.indexOf(SheetsValuesResponse.LABEL_QUESTIONS);
    int typeIndex = headers.indexOf(SheetsValuesResponse.LABEL_TYPE);

    List<QuestionInternal> questions = new ArrayList<>();
    for (List<String> sheetsQuestion : sheetsQuestions) {
      String id = sheetsQuestion.get(idIndex);
      String question = sheetsQuestion.get(questionIndex);
      String type = sheetsQuestion.get(typeIndex);

      switch (type) {
        case SheetsValuesResponse.QUESTION_TYPE_NUMBERED:
          questions.add(
              new MultipleChoiceQuestion(
                  id, question, Type.NUMBERED, parseMcAnswers(headers, sheetsQuestion)));
          break;
        case SheetsValuesResponse.QUESTION_TYPE_MC:
          questions.add(
              new MultipleChoiceQuestion(
                  id, question, Type.MC, parseMcAnswers(headers, sheetsQuestion)));
          break;
        case SheetsValuesResponse.QUESTION_TYPE_LONG:
          questions.add(
              new OpenEndedQuestion(
                  id,
                  question,
                  OpenEndedQuestion.Type.LONG,
                  parseOpenEndedQuestion(headers, sheetsQuestion)));
          break;
        case SheetsValuesResponse.QUESTION_TYPE_SHORT:
          questions.add(
              new OpenEndedQuestion(
                  id,
                  question,
                  OpenEndedQuestion.Type.SHORT,
                  parseOpenEndedQuestion(headers, sheetsQuestion)));
          break;
        default:
          Log.d("UserSneak", "Unsupported question type: " + type);
          break;
      }
    }

    // Parse & Validate Survey Status
    String statusError = statusError(status);
    if (!Strings.isNullOrEmpty(statusError)) {
      return createMalformed(statusError, surveyName);
    }
    SurveyStatus surveyStatus = convert(status.get(1));

    return new Survey(surveyName, surveyStatus, questions);
  }

  private static List<McAnswer> parseMcAnswers(List<String> headers, List<String> question) {
    // TODO(allen): Consider surfacing errors here:
    //  - logic not matching up
    //  - next question doesn't point to an accurate ID
    //  - answer doesn't have logic/next question

    List<McAnswer> answers = new ArrayList<>();
    int answerIndex = headers.indexOf(SheetsValuesResponse.LABEL_ANSWER);
    for (int i = answerIndex; i + 2 < question.size(); i += 3) {
      String[] answerTexts = question.get(i).split("\\|");
      Logic logic =
          question.get(i + 1).equals(SheetsValuesResponse.QUESTION_LOGIC_JUMP)
              ? Logic.JUMP
              : Logic.END;
      String nextQuestion = question.get(i + 2);
      answers.addAll(
          Arrays.stream(answerTexts)
              .filter(text -> !Strings.isNullOrEmpty(text))
              .map(String::trim)
              .map(
                  text -> {
                    if (logic == Logic.JUMP) {
                      return new McAnswer(text, Logic.JUMP, nextQuestion);
                    } else {
                      return new McAnswer(text, Logic.END);
                    }
                  })
              .collect(toList()));
    }

    return answers;
  }

  private static OpenEndedAnswer parseOpenEndedQuestion(
      List<String> headers, List<String> question) {
    int logicIndex = headers.indexOf(SheetsValuesResponse.LABEL_LOGIC);
    Logic logic =
        question.get(logicIndex).equals(SheetsValuesResponse.QUESTION_LOGIC_JUMP)
            ? Logic.JUMP
            : Logic.END;
    if (logic == Logic.JUMP) {
      int nextQuestionIndex = headers.indexOf(SheetsValuesResponse.LABEL_NEXT_QUESTION);
      return new OpenEndedAnswer(logic, question.get(nextQuestionIndex));
    } else {
      return new OpenEndedAnswer(logic);
    }
  }

  private static String headerError(List<String> headers) {
    if (headers == null) {
      return "Survey is missing labels above the questions. Check the template doc.";
    }

    if (!headers.contains(SheetsValuesResponse.LABEL_ID)) {
      return "Survey is missing 'ID' header";
    } else if (!headers.contains(SheetsValuesResponse.LABEL_QUESTIONS)) {
      return "Survey is missing 'Question' header";
    } else if (!headers.contains(SheetsValuesResponse.LABEL_TYPE)) {
      return "Survey is missing 'type' header";
    } else if (!headers.contains(SheetsValuesResponse.LABEL_ANSWER)) {
      return "Survey is missing 'answer' header";
    } else if (!headers.contains(SheetsValuesResponse.LABEL_LOGIC)) {
      return "Survey is missing 'logic' header";
    } else if (!headers.contains(SheetsValuesResponse.LABEL_NEXT_QUESTION)) {
      return "Survey is missing 'next question' header";
    }
    return "";
  }

  private static SurveyStatus convert(String status) {
    switch (status) {
      case SheetsValuesResponse.STATUS_LIVE:
        return SurveyStatus.AVAILABLE;
      case SheetsValuesResponse.STATUS_DRAFT:
      case SheetsValuesResponse.STATUS_COMPLETED:
        return SurveyStatus.NO_SURVEY;
      default:
        throw new IllegalArgumentException("Unknown status: " + status);
    }
  }

  private static String statusError(List<String> status) {
    if (status == null) {
      return "Survey Status row is missing from sheet.";
    } else if (status.size() < 2) {
      return "Survey Status row is missing status";
    } else {
      String val = status.get(1);
      switch (val) {
        case SheetsValuesResponse.STATUS_DRAFT:
        case SheetsValuesResponse.STATUS_LIVE:
        case SheetsValuesResponse.STATUS_COMPLETED:
          return "";
        default:
          return "Unable to handle survey status: " + status.get(1);
      }
    }
  }

  private static Survey createMalformed(String justification, String name) {
    Log.e("UserSneak", "Malformed Survey: " + justification);
    return new Survey(name, SurveyStatus.SURVEY_MALFORMED, ImmutableList.of());
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
