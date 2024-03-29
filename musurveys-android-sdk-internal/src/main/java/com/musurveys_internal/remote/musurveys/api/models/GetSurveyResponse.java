package com.musurveys_internal.remote.musurveys.api.models;

import java.util.List;

public class GetSurveyResponse {
  public ServerSurvey survey;
  public String errorMessage;

  public static class ServerAnswer {
    public String logic;
    public String nextQuestion;
    public String text;
  }

  public static class ServerOpenEndedAnswer {
    public String logic;
    public String nextQuestion;
  }

  public static class ServerQuestion {
    public String id;
    public String type;
    public List<ServerAnswer> answers;
    public ServerOpenEndedAnswer openEndedAnswer;
    public String question;
  }

  public static class ServerSurvey {
    public String surveyName;
    public String status;
    public List<ServerQuestion> questions;
  }
}


