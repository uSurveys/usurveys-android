package com.musurveys_internal.remote.sheets.api;

import androidx.annotation.VisibleForTesting;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musurveys_internal.BuildConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/** Class for accessing retrofit and executing requests to Google Sheets API. */
public class SheetsServiceGenerator {

  private static final String BASE_URL = "https://sheets.googleapis.com/";

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

  private static SheetsApi sheetsApiService = null;

  public static SheetsApi get() {
    if (sheetsApiService == null) {
      sheetsApiService = retrofit.create(SheetsApi.class);
    }
    return sheetsApiService;
  }

  @VisibleForTesting
  public static void setForTesting(SheetsApi sheetsApi) {
    sheetsApiService = sheetsApi;
  }

  private static HttpLoggingInterceptor buildLoggingInterceptor() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);
    return interceptor;
  }
}
