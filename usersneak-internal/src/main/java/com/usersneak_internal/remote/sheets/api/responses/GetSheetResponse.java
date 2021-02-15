package com.usersneak_internal.remote.sheets.api.responses;

import androidx.annotation.VisibleForTesting;
import com.usersneak_internal.remote.sheets.api.responses.GetSheetResponse.ServerSheet.ServerSheetProperties;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetSheetResponse {

  public List<ServerSheet> sheets;

  public static class ServerSheet {

    public ServerSheetProperties properties;

    public static class ServerSheetProperties {

      public String title;
    }
  }

  @VisibleForTesting
  public static GetSheetResponse createForTesting(String... events) {
    GetSheetResponse response = new GetSheetResponse();
    response.sheets =
        Arrays.stream(events)
            .map(
                event -> {
                  ServerSheet sheet = new ServerSheet();
                  sheet.properties = new ServerSheetProperties();
                  sheet.properties.title = event;
                  return sheet;
                })
            .collect(Collectors.toList());
    return response;
  }
}
