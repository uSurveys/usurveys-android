package com.musurveys_internal.remote.musurveys.api;

import com.musurveys_internal.remote.musurveys.api.models.GetSurveyResponse;
import com.musurveys_internal.remote.musurveys.api.models.PostSurveyResultBody;
import com.musurveys_internal.utils.network.PostResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MuSurveysApi {

  @GET("/api/surveys")
  Call<GetSurveyResponse> getSurvey(
      @Header("api-key") String apiKey,
      @Query("sheetId") String sheetId,
      @Query("eventName") String eventName);

  @POST("/api/surveys/results")
  Call<PostResponse> postSurveyResults(
      @Header("api-key") String apiKey, @Body PostSurveyResultBody body);
}
