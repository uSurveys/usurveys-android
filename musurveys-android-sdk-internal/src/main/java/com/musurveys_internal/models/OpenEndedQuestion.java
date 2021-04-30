package com.musurveys_internal.models;

import com.google.common.base.Optional;
import com.musurveys_api.MuSurveysQuestion;
import java.util.List;

public final class OpenEndedQuestion extends QuestionInternal {

  public enum Type {
    SHORT,
    LONG;

    public static Type from(String serverValue) {
      switch (serverValue) {
        case "SHORT_ANSWER":
          return SHORT;
        case "LONG_ANSWER":
          return LONG;
      }
      throw new IllegalArgumentException("None exists: " + serverValue);
    }
  }

  private final String id;
  private final String questionText;
  private final Type type;
  private final OpenEndedAnswer openEndedAnswer;

  public OpenEndedQuestion(
      String id, String questionText, Type type, OpenEndedAnswer openEndedAnswer) {
    this.id = id;
    this.questionText = questionText;
    this.type = type;
    this.openEndedAnswer = openEndedAnswer;
  }

  @Override
  public Optional<String> getNextQuestion(String answer) {
    switch (openEndedAnswer.getLogic()) {
      case JUMP:
        return Optional.of(openEndedAnswer.getNextQuestion());
      case END:
        return Optional.absent();
      default:
        throw new IllegalArgumentException("Unhandled type: " + type);
    }
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getQuestion() {
    return questionText;
  }

  @Override
  public List<String> getAnswers() {
    return null;
  }

  @Override
  public MuSurveysQuestion.Type getType() {
    return type == Type.SHORT
        ? MuSurveysQuestion.Type.SHORT_ANSWER
        : MuSurveysQuestion.Type.LONG_ANSWER;
  }
}
