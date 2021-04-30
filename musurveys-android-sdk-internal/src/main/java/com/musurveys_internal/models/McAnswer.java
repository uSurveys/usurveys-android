package com.musurveys_internal.models;

public final class McAnswer extends Answer {

  private final String text;

  public McAnswer(String text, Logic logic) {
    super(logic);
    this.text = text;
  }

  public McAnswer(String text, Logic logic, String nextQuestion) {
    super(logic, nextQuestion);
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
