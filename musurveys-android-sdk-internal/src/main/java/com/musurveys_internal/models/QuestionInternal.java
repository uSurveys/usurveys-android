package com.musurveys_internal.models;

import com.google.common.base.Optional;
import com.musurveys_api.MuSurveysQuestion;

public abstract class QuestionInternal extends MuSurveysQuestion {

  /**
   * Returns the next {@link MuSurveysQuestion#getId()} question if there is one, absent otherwise.
   */
  public abstract Optional<String> getNextQuestion(String answer);
}
