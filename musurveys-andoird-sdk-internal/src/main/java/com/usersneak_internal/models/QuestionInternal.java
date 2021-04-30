package com.usersneak_internal.models;

import com.google.common.base.Optional;
import com.usersneak_api.UserSneakQuestion;

public abstract class QuestionInternal extends UserSneakQuestion {

  /**
   * Returns the next {@link UserSneakQuestion#getId()} question if there is one, absent otherwise.
   */
  public abstract Optional<String> getNextQuestion(String answer);
}
