package com.musurveys_internal.models;

import static com.musurveys_internal.models.Answer.Logic.JUMP;

import com.google.common.base.Optional;
import com.musurveys_api.MuSurveysQuestion;
import java.util.List;
import java.util.stream.Collectors;

public final class MultipleChoiceQuestion extends QuestionInternal {

  public enum Type {
    NUMBERED,
    MC,
  }

  private final String id;
  private final String questionText;
  private final Type type;
  private final List<McAnswer> answers;

  public MultipleChoiceQuestion(String id, String questionText, Type type, List<McAnswer> answers) {
    this.id = id;
    this.questionText = questionText;
    this.type = type;
    this.answers = answers;
  }

  @Override
  public Optional<String> getNextQuestion(String answer) {
    McAnswer mcAnswer = answers.stream().filter(a -> a.getText().equals(answer)).findFirst().get();
    return mcAnswer.getLogic() == JUMP
        ? Optional.of(mcAnswer.getNextQuestion())
        : Optional.absent();
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
    return answers.stream().map(McAnswer::getText).collect(Collectors.toList());
  }

  @Override
  public MuSurveysQuestion.Type getType() {
    return type == Type.NUMBERED
        ? MuSurveysQuestion.Type.NUMBERED
        : MuSurveysQuestion.Type.MULTIPLE_CHOICE;
  }
}
