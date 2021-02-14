package com.usersneak_internal.models;

import static com.usersneak_internal.models.Answer.Logic.JUMP;

import com.google.common.base.Optional;
import com.usersneak_api.UserSneakQuestion;
import java.util.List;

public final class MultipleChoiceQuestion extends QuestionInternal {

  public enum Type {
    NUMBERED,
    MC,
  }

  private final String id;
  private final String questionText;
  private final Type type;
  private final List<McAnswer> answers;
  private final String answer;

  public MultipleChoiceQuestion(
      String id, String questionText, Type type, List<McAnswer> answers, String answer) {
    this.id = id;
    this.questionText = questionText;
    this.type = type;
    this.answers = answers;
    this.answer = answer;
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
    return null;
  }

  @Override
  public String getAnswer() {
    return answer;
  }

  @Override
  public UserSneakQuestion.Type getType() {
    return type == Type.NUMBERED
        ? UserSneakQuestion.Type.NUMBERED
        : UserSneakQuestion.Type.MULTIPLE_CHOICE;
  }
}
