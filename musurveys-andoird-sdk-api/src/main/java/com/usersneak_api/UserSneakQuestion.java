package com.usersneak_api;

import java.util.List;

public abstract class UserSneakQuestion {

  public enum Type {
    NUMBERED,
    MULTIPLE_CHOICE,
    LONG_ANSWER,
    SHORT_ANSWER,
  }

  /** Returns the unique id for this question. */
  public abstract String getId();

  /** Returns the question text (ex: "What do you like about this feature?"). */
  public abstract String getQuestion();

  /** Returns the list of possible answers for multiple choice questions. Null otherwise. */
  public abstract List<String> getAnswers();

  /** Returns the question {@link Type type}. */
  public abstract Type getType();
}
