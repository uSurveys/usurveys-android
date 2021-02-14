package com.usersneak_internal.models;

import com.google.common.base.Optional;
import com.usersneak_api.UserSneakQuestion;
import java.util.List;

public final class OpenEndedQuestion extends QuestionInternal {

  public enum Type {
    SHORT,
    LONG,
  }

  private final String id;
  private final String questionText;
  private final Type type;
  private final OpenEndedAnswer openEndedAnswer;
  private final String answer;

  public OpenEndedQuestion(
      String id, String questionText, Type type, OpenEndedAnswer openEndedAnswer, String answer) {
    this.id = id;
    this.questionText = questionText;
    this.type = type;
    this.openEndedAnswer = openEndedAnswer;
    this.answer = answer;
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
  public String getAnswer() {
    return answer;
  }

  @Override
  public UserSneakQuestion.Type getType() {
    return type == Type.SHORT
        ? UserSneakQuestion.Type.SHORT_ANSWER
        : UserSneakQuestion.Type.LONG_ANSWER;
  }
}
