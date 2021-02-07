package com.usersneak_internal;

import androidx.annotation.Nullable;
import com.usersneak_api.UserSneakQuestion;
import java.util.ArrayList;
import java.util.List;

public final class UserSneakQuestionImpl extends UserSneakQuestion {

  public final String id;
  public final String text;
  public final List<String> answers;
  public final String answer;
  public final Type type;

  UserSneakQuestionImpl(
      String id, String text, @Nullable List<String> answers, String answer, Type type) {
    this.id = id;
    this.text = text;
    this.answers = answers;
    this.answer = answer == null ? "" : answer;
    this.type = type;
  }

  public static UserSneakQuestionImpl createOneToFive(String id, String text, String answer) {
    ArrayList<String> answers = new ArrayList<>(5);
    answers.add("1");
    answers.add("2");
    answers.add("3");
    answers.add("4");
    answers.add("5");
    return new UserSneakQuestionImpl(id, text, answers, answer, Type.ONE_TO_FIVE);
  }

  public static UserSneakQuestionImpl createOneToTen(String id, String text, String answer) {
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
    return new UserSneakQuestionImpl(id, text, answers, answer, Type.ONE_TO_TEN);
  }

  public static UserSneakQuestionImpl createShortAnswer(String id, String text, String answer) {
    return new UserSneakQuestionImpl(id, text, null, answer, Type.SHORT_ANSWER);
  }

  public static UserSneakQuestionImpl createLongAnswer(String id, String text, String answer) {
    return new UserSneakQuestionImpl(id, text, null, answer, Type.LONG_ANSWER);
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
    return answer;
  }

  @Override
  public Type getType() {
    return type;
  }
}
