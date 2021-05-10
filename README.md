# µSurveys Android Client

µSurveys is an application for showing micro-suveys embedded in your own application.
You can learn more and setup an account at https://musurveys.com

## Get Started

Add the dependency to gradle:
```gradle
dependencies {
    // ...
    
    // µSurveys library
    implementation "com.musurveys:musurveys-android-sdk:<latest-release>"
}
```

Configurations in your Application class:
```java
@Override
public void onCreate() {
    super.onCreate();
    // Sign up at musurveys.com, to get an API key.
    UserSneak.get()
        .configureUserSneakApiKey(USER_SNEAK_API_KEY)
        // See docs.usersneak.com to learn how to setup your sheet.
        .configureSheetsId(SHEET_ID)
        // Set the minimum amount of time that must pass before the user is shown another survey.
        // See docs.userneak.com to learn more.
        .configureResurveyWindowMillis(TimeUnit.SECONDS.toMillis(5));
}
```

Track and show surveys like so:
```java
ActivityResultLauncher<Intent> muSurveysLauncher =
    registerForActivityResult(
        new StartActivityForResult(),
        result -> {
            // TODO: Survey complete, update the UI.
        });

StatusCallback callback =
    status -> {
      switch (status) {
        case NO_SURVEY:
        case SURVEY_MALFORMED:
          // TODO: Handle no survey present
          break;

        case AVAILABLE:
          MuSurveys.get().showSurvey(requireActivity(), eventName, muSurveysLauncher);
          break;
      }
    };
MuSurveys.get().track(eventName, callback);
```

## Contributor Guide
### Setup
1. clone the project
2. setup pre-commit
3. Install Android Studio (check gradle properties file, but probably the latest canary version)
4. import into AS
5. Press Run

### Deploy Instructions
1. See scripts/README

### TODO
P0:
1. Implement some error and stacktrace reporting

P1:
1. Make the surveys look prettier
2. Upload demo app to the play store
