package com.usersneak_internal.remote.sheets.api.responses;

import java.util.List;

public class GetSheetResponse {

  public List<ServerSheet> sheets;

  public static class ServerSheet {

    public ServerSheetProperties properties;

    public static class ServerSheetProperties {

      public String sheetId;
      public String title;
    }
  }
}
