package com.usersneak_internal.remote.usersneak.api;

import com.usersneak_internal.remote.usersneak.api.models.GetSurveyResponse;
import com.usersneak_internal.remote.usersneak.api.models.PostSurveyResultBody;
import com.usersneak_internal.utils.network.PostResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserSneakApi {

  @GET("/api/surveys")
  Call<GetSurveyResponse> getSurvey(
      @Header("api-key") String apiKey,
      @Query("sheetId") String sheetId,
      @Query("eventName") String eventName);

  @POST("/api/surveys/results")
  Call<PostResponse> postSurveyResults(
      @Header("api-key") String apiKey, @Body PostSurveyResultBody body);
}
