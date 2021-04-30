package com.musurveys_internal.remote.musurveys.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musurveys_internal.BuildConfig;

import java.util.concurrent.TimeUnit;

import androidx.annotation.VisibleForTesting;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/** Class for accessing retrofit and executing requests to MuSurveys backend. */
public class MuSurveysServiceGenerator {

  private static final String BASE_URL = "https://protected-reef-30340.herokuapp.com/";
  //private static final String BASE_URL = "http://api.musurveys.com/";

  private static final OkHttpClient okHttpClient =
      new OkHttpClient.Builder()
          .connectTimeout(3, TimeUnit.SECONDS)
          .readTimeout(3, TimeUnit.SECONDS)
          .addInterceptor(buildLoggingInterceptor())
          .build();

  private static final Gson gson =
      new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

  private static final Retrofit retrofit =
      new Retrofit.Builder()
          .baseUrl(BASE_URL)
          .client(okHttpClient)
          .addConverterFactory(ScalarsConverterFactory.create())
          .addConverterFactory(GsonConverterFactory.create(gson))
          .build();

  private static MuSurveysApi muSurveysApiService = null;

  public static MuSurveysApi get() {
    if (muSurveysApiService == null) {
      muSurveysApiService = retrofit.create(MuSurveysApi.class);
    }
    return muSurveysApiService;
  }

  @VisibleForTesting
  public static void setForTesting(MuSurveysApi MuSurveysApi) {
    muSurveysApiService = MuSurveysApi;
  }

  private static HttpLoggingInterceptor buildLoggingInterceptor() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);
    return interceptor;
  }
}
