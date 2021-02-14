package com.usersneak_internal.remote.sheets.api.responses;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SheetsValuesResponse {

  public static final String STATUS = "survey status";
  public static final String STATUS_DRAFT = "draft";
  public static final String STATUS_LIVE = "live";
  public static final String STATUS_COMPLETED = "completed";

  public static final String LABEL_ID = "id";
  public static final String LABEL_QUESTIONS = "question";
  public static final String LABEL_TYPE = "type";
  public static final String LABEL_ANSWER = "answer";
  public static final String LABEL_LOGIC = "logic";
  public static final String LABEL_NEXT_QUESTION = "next question";

  public static final String QUESTION_TYPE_NUMBERED = "numbered";
  public static final String QUESTION_TYPE_SHORT = "short answer";
  public static final String QUESTION_TYPE_LONG = "long answer";
  public static final String QUESTION_TYPE_MC = "multiple choice";

  public static final String QUESTION_LOGIC_JUMP = "jump";
  public static final String QUESTION_LOGIC_END = "end";

  public List<List<String>> values;

  public static SheetsValuesResponse clean(SheetsValuesResponse response) {
    List<List<String>> cleanValues = new ArrayList<>(response.values.size());
    Map<Integer, Boolean> shouldClean = new HashMap<>();

    for (List<String> row : response.values) {
      if (row == null || row.isEmpty()) {
        continue;
      }

      String id = clean(row.get(0));
      if (id.equals(STATUS)) {
        cleanValues.add(row.stream().map(SheetsValuesResponse::clean).collect(Collectors.toList()));

      } else if (id.equals(LABEL_ID)) {
        List<String> labels =
            row.stream().map(SheetsValuesResponse::clean).collect(Collectors.toList());
        cleanValues.add(labels);

        for (int i = 0; i < labels.size(); i++) {
          String label = labels.get(i);
          shouldClean.put(i, label.equals(LABEL_TYPE) || label.equals(LABEL_LOGIC));
        }

      } else {
        List<String> question = new ArrayList<>(shouldClean.size());
        for (int i = 0; i < shouldClean.size(); i++) {
          // Make all question rows the same length as each other and the labels row.
          if (row.size() < i + 1) {
            question.add("");
            continue;
          }

          question.add(
              shouldClean.getOrDefault(i, false)
                  ? clean(row.get(i))
                  : Strings.nullToEmpty(row.get(i)).trim());
        }
        cleanValues.add(question);
      }
    }

    SheetsValuesResponse updated = new SheetsValuesResponse();
    updated.values = cleanValues;
    return updated;
  }

  private static String clean(String val) {
    return Strings.nullToEmpty(val).toLowerCase().trim();
  }
}
