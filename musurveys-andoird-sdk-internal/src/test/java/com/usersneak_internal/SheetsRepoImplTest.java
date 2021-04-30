package com.usersneak_internal;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.usersneak.usersneak_testing.FakeCall;
import com.usersneak.usersneak_testing.LiveDataTestUtil;
import com.usersneak_api.UserSneakApi.StatusCallback.SurveyStatus;
import com.usersneak_api.UserSneakQuestion.Type;
import com.usersneak_internal.models.QuestionInternal;
import com.usersneak_internal.models.Survey;
import com.usersneak_internal.remote.sheets.api.SheetsApi;
import com.usersneak_internal.remote.sheets.api.SheetsServiceGenerator;
import com.usersneak_internal.remote.sheets.api.responses.GetSheetResponse;
import com.usersneak_internal.remote.sheets.api.responses.SheetsValuesResponse;
import com.usersneak_internal.remote.sheets.repo.SheetsModule;
import com.usersneak_internal.utils.network.RequestStatus;
import com.usersneak_internal.utils.network.RequestStatus.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import retrofit2.Response;

/** Tests for {@link com.usersneak_internal.remote.sheets.repo.SheetsRepoImpl}. */
@RunWith(AndroidJUnit4.class)
@Config(sdk = 28) // TODO(allen): Upgrade to AS Arctic Fox and Java 11, then remove this config
public final class SheetsRepoImplTest {

  private final String[] headers = {
    SheetsValuesResponse.LABEL_ID,
    SheetsValuesResponse.LABEL_QUESTIONS,
    SheetsValuesResponse.LABEL_TYPE,
    SheetsValuesResponse.LABEL_ANSWER,
    SheetsValuesResponse.LABEL_LOGIC,
    SheetsValuesResponse.LABEL_NEXT_QUESTION,
    SheetsValuesResponse.LABEL_ANSWER,
    SheetsValuesResponse.LABEL_LOGIC,
    SheetsValuesResponse.LABEL_NEXT_QUESTION,
  };
  private final String[][] sheet = {
    {SheetsValuesResponse.STATUS, SheetsValuesResponse.STATUS_LIVE},
    headers,
    {"1", "q1", "numbered", "1|2|3|4", "jump", "2", "5", "jump", "3"},
    {"2", "q2", "short answer", "", "jump", "4"},
    {"3", "q3", "long answer", "", "end", ""},
    {"4", "q4", "multiple choice", "Run it|\nJump it|\nCrush it", "end", ""}
  };

  private final SheetsApi mockSheetsApi = mock(SheetsApi.class);

  @Before
  public void setup() {
    SheetsServiceGenerator.setForTesting(mockSheetsApi);
    SheetsModule.clearForTesting();
  }

  @Test
  public void testSheetsRepoSetup() throws InterruptedException {
    String eventName = "test";
    when(mockSheetsApi.getSheets(anyString(), anyString()))
        .thenReturn(
            FakeCall.success(Response.success(GetSheetResponse.createForTesting(eventName))));
    when(mockSheetsApi.getValues(
            anyString(), matches(String.format("'%s'!A1:Z50", eventName)), anyString()))
        .thenReturn(
            FakeCall.success(Response.success(SheetsValuesResponse.createForTesting(sheet))));

    RequestStatus<Optional<Survey>> response =
        LiveDataTestUtil.getOrAwaitValue(SheetsModule.getInstance().getSurvey(eventName));

    assertThat(response.status).isEqualTo(Status.SUCCESS);
    assertThat(response.getResult()).isPresent();
  }

  @Test
  public void testSurveyConversion() throws InterruptedException {
    String eventName = "test";
    when(mockSheetsApi.getSheets(anyString(), anyString()))
        .thenReturn(
            FakeCall.success(Response.success(GetSheetResponse.createForTesting(eventName))));
    when(mockSheetsApi.getValues(
            anyString(), matches(String.format("'%s'!A1:Z50", eventName)), anyString()))
        .thenReturn(
            FakeCall.success(Response.success(SheetsValuesResponse.createForTesting(sheet))));

    RequestStatus<Optional<Survey>> response =
        LiveDataTestUtil.getOrAwaitValue(SheetsModule.getInstance().getSurvey(eventName));

    Survey survey = response.getResult().get();
    assertThat(survey.surveyName).isEqualTo(eventName);
    assertThat(survey.status).isEqualTo(SurveyStatus.AVAILABLE);

    ImmutableList<QuestionInternal> questions = survey.questions;
    assertThat(questions).hasSize(4);
    assertThat(questions.get(0).getId()).isEqualTo("1");
    assertThat(questions.get(0).getType()).isEqualTo(Type.NUMBERED);
    assertThat(questions.get(0).getAnswers()).containsExactly("1", "2", "3", "4", "5");
    assertThat(questions.get(0).getNextQuestion("1")).hasValue("2");
    assertThat(questions.get(0).getNextQuestion("2")).hasValue("2");
    assertThat(questions.get(0).getNextQuestion("3")).hasValue("2");
    assertThat(questions.get(0).getNextQuestion("4")).hasValue("2");
    assertThat(questions.get(0).getNextQuestion("5")).hasValue("3");

    assertThat(questions.get(1).getId()).isEqualTo("2");
    assertThat(questions.get(1).getType()).isEqualTo(Type.SHORT_ANSWER);
    assertThat(questions.get(1).getNextQuestion("short")).hasValue("4");
    assertThat(questions.get(1).getAnswers()).isNull();

    assertThat(questions.get(2).getId()).isEqualTo("3");
    assertThat(questions.get(2).getType()).isEqualTo(Type.LONG_ANSWER);
    assertThat(questions.get(2).getNextQuestion("long")).isAbsent();
    assertThat(questions.get(2).getAnswers()).isNull();

    assertThat(questions.get(3).getId()).isEqualTo("4");
    assertThat(questions.get(3).getType()).isEqualTo(Type.MULTIPLE_CHOICE);
    assertThat(questions.get(3).getAnswers()).containsExactly("Run it", "Jump it", "Crush it");
    assertThat(questions.get(3).getNextQuestion("Run it")).isAbsent();
    assertThat(questions.get(3).getNextQuestion("Jump it")).isAbsent();
    assertThat(questions.get(3).getNextQuestion("Crush it")).isAbsent();
  }
}
