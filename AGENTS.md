# Repository Guidelines

## Project Structure & Module Organization
`FeverTest` is a single-module Android app. Root Gradle files live in `/` (`build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`). App code is in `app/src/main`: Kotlin sources under `java/com/luisnavarro/fevertest`, Compose theme files under `ui/theme`, and Android resources under `res/`. Local JVM tests live in `app/src/test`; device and emulator tests live in `app/src/androidTest`.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root:

- `./gradlew app:assembleDebug` builds a debug APK.
- `./gradlew app:installDebug` installs the debug build on a connected device or emulator.
- `./gradlew app:testDebugUnitTest` runs local JUnit tests from `app/src/test`.
- `./gradlew app:connectedDebugAndroidTest` runs instrumentation tests on a connected device/emulator.
- `./gradlew app:lintDebug` runs Android lint for the debug variant.
- `./gradlew app:build` performs a full build plus checks for the app module.

## Coding Style & Naming Conventions
This project uses Kotlin, Jetpack Compose, and Kotlin DSL Gradle files. Follow standard Kotlin formatting: 4-space indentation, one top-level declaration per responsibility, and trailing commas only where they improve diffs. Use `PascalCase` for classes and composables (`MainActivity`, `GreetingPreview`), `camelCase` for functions and properties (`onCreate`, `innerPadding`), and keep packages aligned with `com.luisnavarro.fevertest`. Place reusable UI theme code in `ui/theme` and keep resource names lowercase with underscores, for example `ic_launcher_foreground.xml`.

## Testing Guidelines
Prefer fast unit tests in `app/src/test` for pure logic and reserve `app/src/androidTest` for Android framework or UI behavior. Name test classes after the target (`TemperatureParserTest`) and test methods with behavior-style names such as `returnsNull_forBlankInput`. Run unit tests before every PR; run instrumentation tests when UI, manifest, or Android integrations change.

## Commit & Pull Request Guidelines
Local `.git` metadata is not available in this workspace, so no repository-specific commit pattern could be verified. Use short, imperative commit subjects like `Add fever input validation`. Keep pull requests focused, describe user-visible changes, list test coverage (`app:testDebugUnitTest`, device testing when relevant), and attach screenshots for Compose UI changes.

## Configuration Tips
`local.properties` is machine-specific and should only contain local SDK paths or developer overrides. Do not store secrets in the repository; prefer Gradle properties or environment variables for any future API keys.
