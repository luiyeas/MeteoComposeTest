# Fever Weather Test

Single-screen Android app that fetches the current weather for a random location and lets the user refresh to get another one.

## Requirements

- Android Studio with the Android SDK installed
- A running emulator or connected device
- `OPEN_WEATHER_API_KEY` configured before building
- `MAPS_API_KEY` optional: if omitted, the location card falls back to a visual placeholder instead of a live map

## Local configuration

Create or update `local.properties` in the project root:

```properties
OPEN_WEATHER_API_KEY=your_openweather_key
MAPS_API_KEY=your_google_maps_key
```

`OPEN_WEATHER_API_KEY` is required because the project no longer ships with a hardcoded fallback. This keeps API credentials out of the repository and matches normal production handling.

## Common commands

```bash
./gradlew app:assembleDebug
./gradlew app:testDebugUnitTest
./gradlew app:connectedDebugAndroidTest
./gradlew app:lintDebug
```

## Notes

- The map in the location card uses Google Maps Lite Mode when `MAPS_API_KEY` is present.
- UI tests force the location card to render its placeholder, so they do not depend on a live Maps key.
