package com.musurveys_internal.models;

import com.google.common.base.Optional;

public abstract class Answer {

  public enum Logic {
    JUMP,
    END,
  }

  private final Logic logic;
  private final Optional<String> nextQuestion;

  public Answer(Logic logic) {
    this.logic = logic;
    nextQuestion = Optional.absent();
  }

  public Answer(Logic logic, String nextQuestion) {
    this.logic = logic;
    this.nextQuestion = Optional.fromNullable(nextQuestion);
  }

  public final Logic getLogic() {
    return logic;
  }

  public final String getNextQuestion() {
    return nextQuestion.get();
  }
}
