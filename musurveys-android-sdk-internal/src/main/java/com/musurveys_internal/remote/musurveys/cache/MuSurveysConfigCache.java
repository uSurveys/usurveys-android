package com.musurveys_internal.remote.musurveys.cache;

import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme;
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKey.Builder;
import androidx.security.crypto.MasterKey.KeyScheme;
import com.google.common.base.Strings;
import com.musurveys_internal.application.AppContext;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class MuSurveysConfigCache {

  private static MuSurveysConfigCache CONFIG_CACHE = null;

  private final SharedPreferences prefs;

  private String apiKey = null;
  private String sheetId = null;
  private long resurveyWindow = -1;
  private String customerId = null;
  private String userId = null;

  @Nullable
  public static MuSurveysConfigCache get() {
    if (CONFIG_CACHE == null) {
      try {
        // TODO(allen): create an in memory cache if there is failure here for some reason.
        //  we cannot crash.
        CONFIG_CACHE = new MuSurveysConfigCache();
      } catch (GeneralSecurityException | IOException ignored) {
      }
    }
    return CONFIG_CACHE;
  }

  private MuSurveysConfigCache() throws GeneralSecurityException, IOException {
    MasterKey masterKey =
        new Builder(AppContext.get(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setUserAuthenticationRequired(false)
            .setRequestStrongBoxBacked(false)
            .setKeyScheme(KeyScheme.AES256_GCM)
            .build();

    prefs =
        EncryptedSharedPreferences.create(
            AppContext.get(),
            "musurveys_configs",
            masterKey,
            PrefKeyEncryptionScheme.AES256_SIV,
            PrefValueEncryptionScheme.AES256_GCM);
  }

  public void storeApiKey(String apiKey) {
    this.apiKey = apiKey;
    prefs.edit().putString("user_sneak_api_key", apiKey).apply();
  }

  public String getApiKey() {
    if (Strings.isNullOrEmpty(apiKey)) {
      apiKey = prefs.getString("user_sneak_api_key", "");
    }
    return apiKey;
  }

  public void storeSheetId(String sheetId) {
    this.sheetId = sheetId;
    prefs.edit().putString("user_sneak_sheet_id", sheetId).apply();
  }

  public String getSheetId() {
    if (Strings.isNullOrEmpty(sheetId)) {
      sheetId = prefs.getString("user_sneak_sheet_id", "");
    }
    return sheetId;
  }

  public void setResurveyWindow(long millis) {
    resurveyWindow = millis;
    prefs.edit().putLong("user_sneak_resurvey_window", millis).apply();
  }

  public long getResurveyWindow() {
    if (resurveyWindow == -1) {
      resurveyWindow = prefs.getLong("user_sneak_resurvey_window", TimeUnit.DAYS.toMillis(7));
    }
    return resurveyWindow;
  }

  public void recordSurveyTimestamp(long timestampMillis) {
    prefs.edit().putLong("user_sneak_last_survey_time", timestampMillis).apply();
  }

  public long lastSurveyTimeMillis() {
    return prefs.getLong("user_sneak_last_survey_time", -1);
  }

  public void setCustomerId(String id) {
    this.customerId = id;
    prefs.edit().putString("user_sneak_customer_id", id).apply();
  }

  public String getCustomerId() {
    if (Strings.isNullOrEmpty(customerId)) {
      customerId = prefs.getString("user_sneak_customer_id", "");
    }
    return customerId;
  }

  public String getUserId() {
    // Check in memory cache, else fetch from prefs
    if (Strings.isNullOrEmpty(userId)) {
      userId = prefs.getString("user_sneak_user_id", "");

      // if prefs is empty, create a uuid. This value is immutable.
      if (Strings.isNullOrEmpty(userId)) {
        userId = UUID.randomUUID().toString();
        prefs.edit().putString("user_sneak_user_id", userId).apply();
      }
    }
    return userId;
  }

  public void logout() {
    this.userId = "";
    this.customerId = "";
    prefs.edit()
        .putString("user_sneak_user_id", "")
        .putString("user_sneak_customer_id", "")
        .putLong("user_sneak_last_survey_time", 0)
        .apply();
  }
}
