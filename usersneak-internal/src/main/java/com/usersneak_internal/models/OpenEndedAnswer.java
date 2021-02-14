package com.usersneak_internal.models;

public final class OpenEndedAnswer extends Answer {

  public OpenEndedAnswer(Logic logic) {
    super(logic);
  }

  public OpenEndedAnswer(Logic logic, String nextQuestion) {
    super(logic, nextQuestion);
  }
}
