package com.usersneak_internal.models;

import androidx.annotation.Nullable;
import com.google.common.base.Optional;
import com.usersneak_api.UserSneakQuestion;
import java.util.ArrayList;
import java.util.List;

public final class Question extends UserSneakQuestion {

  public final String id;
  public final String text;
  public final List<String> answers;
  public final Optional<String> answer;
  public final Type type;

  Question(
      String id, String text, @Nullable List<String> answers, Optional<String> answer, Type type) {
    this.id = id;
    this.text = text;
    this.answers = answers;
    this.answer = answer;
    this.type = type;
  }

  public static Question createOneToFive(String id, String text, Optional<String> answer) {
    ArrayList<String> answers = new ArrayList<>(5);
    answers.add("1");
    answers.add("2");
    answers.add("3");
    answers.add("4");
    answers.add("5");
    return new Question(id, text, answers, answer, Type.ONE_TO_FIVE);
  }

  public static Question createOneToTen(String id, String text, Optional<String> answer) {
    ArrayList<String> answers = new ArrayList<>(5);
    answers.add("1");
    answers.add("2");
    answers.add("3");
    answers.add("4");
    answers.add("5");
    answers.add("6");
    answers.add("7");
    answers.add("8");
    answers.add("9");
    answers.add("10");
    return new Question(id, text, answers, answer, Type.ONE_TO_TEN);
  }

  public static Question createShortAnswer(String id, String text, Optional<String> answer) {
    return new Question(id, text, null, answer, Type.SHORT_ANSWER);
  }

  public static Question createLongAnswer(String id, String text, Optional<String> answer) {
    return new Question(id, text, null, answer, Type.LONG_ANSWER);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getQuestion() {
    return text;
  }

  @Override
  public List<String> getAnswers() {
    return answers;
  }

  @Override
  public String getAnswer() {
    return answer.or("");
  }

  public Optional<String> getAnswerOptional() {
    return answer;
  }

  @Override
  public Type getType() {
    return type;
  }
}
