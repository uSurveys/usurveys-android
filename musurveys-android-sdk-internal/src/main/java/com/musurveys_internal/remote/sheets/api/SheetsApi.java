package com.musurveys_internal.remote.sheets.api;

import com.musurveys_internal.remote.sheets.api.responses.GetSheetResponse;
import com.musurveys_internal.remote.sheets.api.responses.SheetsValuesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// See https://developers.google.com/sheets/api/reference/rest/
public interface SheetsApi {

  @GET("/v4/spreadsheets/{spreadsheetId}/values/{range}?majorDimension=ROWS")
  Call<SheetsValuesResponse> getValues(
      @Path("spreadsheetId") String sheetId,
      @Path("range") String range,
      @Query("key") String apiKey);

  @GET("/v4/spreadsheets/{spreadsheetId}")
  Call<GetSheetResponse> getSheets(
      @Path("spreadsheetId") String sheetId, @Query("key") String apiKey);
}
