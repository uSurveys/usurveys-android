package com.usersneak.demo.analytics;

import com.usersneak.demo.BuildConfig;
import com.usersneak.demo.DemoApplication;
import java.util.HashMap;

public final class AppAnalytics {

  private static final String PROP_APP_VERSION = "app_version";
  private static final String PROP_PLATFORM = "platform";
  private static final String VALUE_PLATFORM = "android";

  public static void track(String eventName) {
    track(eventName, new HashMap<>());
  }

  public static void track(String eventName, String key, Object value) {
    HashMap<String, Object> properties = new HashMap<>();
    properties.put(key, value);
    track(eventName, properties);
  }

  public static void track(String eventName, HashMap<String, Object> properties) {
    properties.put(PROP_APP_VERSION, BuildConfig.VERSION_NAME);
    properties.put(PROP_PLATFORM, VALUE_PLATFORM);
    AppAnalyticsWorker.track(DemoApplication.getContext(), eventName, properties);
  }

  /**
   * Helper to generate a hash map that contains the first {key: value} mapping passed.
   *
   * @param key String
   * @param value String
   * @return HashMap {key: value}
   */
  public static HashMap<String, String> createStringProps(String key, String value) {
    HashMap<String, String> props = new HashMap<>();
    props.put(key, value);
    return props;
  }

  private AppAnalytics() {}
}
