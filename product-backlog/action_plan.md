# Action Plan

## Goal

Build a one-screen Android weather app that:

- generates a valid random latitude and longitude
- fetches current weather for that location
- renders the result in a polished Material 3 screen
- lets the user refresh and repeat the flow
- handles loading and error states cleanly
- is backed by meaningful automated tests

This plan is based on `documentation/Instructions.md`, the recruiter notes, and the light/dark mockups in `design/`.

## Product Scope

### Core user flows

1. On app launch, the app generates a valid random location and loads weather for it.
2. The user sees the current weather in a meaningful layout.
3. The user can refresh to generate a new random location and fetch weather again.
4. The user can recover from failures with a retry action.
5. The app supports both light and dark mode.

### Explicit scope decisions

- The app remains single-screen in v1.
- The floating refresh button is the main action.
- The top-left menu icon is visual only in v1 unless new requirements appear.
- The top-right location badge is visual/contextual only in v1.
- The app follows system theme and must look coherent in both light and dark mode.
- The lower “map” section is a visual, non-interactive location card in v1.

## Design and Behavior Contract

### UI sections to implement

- top app bar with title
- hero area with location name, temperature, and coordinates
- main weather summary card
- grid of secondary weather metrics
- location context card in the lower section
- floating refresh CTA

### Important product assumptions

- Use `units=metric` to get Celsius temperatures from OpenWeather.
- OpenWeather current weather returns wind in meters per second, so convert it to km/h in the mapper to match the mock.
- The current weather endpoint does not expose UV index. Do not fabricate this value. Replace that slot with `Local time`, derived from the API response timestamp and timezone offset.
- Random coordinates may point to areas without a recognizable city name. Always show coordinates. Show city/country when available; otherwise use a fallback title such as `Random location`.
- A live map SDK is out of scope unless it becomes trivial and does not require extra platform setup.

### State behavior

- Initial load: show a loading state before the first success or error.
- Refresh after success: keep previous content visible and show progress on the refresh action or as an inline loading hint.
- Error with no previous data: show a dedicated error state with retry.
- Error after previous success: preserve the last successful data and surface a recoverable error message.
- Refresh while already loading: ignore repeated taps or disable the action until the current request finishes.

## Out of Scope

- manual location search
- multi-screen navigation
- offline caching
- interactive maps
- fake or estimated values not provided by the chosen API

## Epic Overview

| Epic | Priority | Outcome |
| --- | --- | --- |
| E1. Foundation | P0 | Clean, testable project baseline |
| E2. Random Weather Data | P0 | Valid coordinates and successful weather retrieval |
| E3. Screen UI | P0 | Main screen implemented to match the mock direction |
| E4. States and Resilience | P0 | Loading, refresh, and error behavior are solid |
| E5. Testing and Quality | P0 | Critical behavior is covered by automated tests |
| E6. Submission Readiness | P1 | README, setup, trade-offs, and review checklist complete |

## Backlog

## E1. Foundation

### [ ] US-01 Define the app structure and dependencies

As a developer, I want a simple and testable app structure so implementation stays fast and maintainable.

Done when:

- [ ] package boundaries are clear (`core`, `data`, `feature/weather` or equivalent)
- [ ] dependencies match the chosen architecture and are justified
- [ ] secret handling is defined without hardcoding personal credentials

Implementation notes:

- Prefer a single app module.
- Prefer constructor injection.
- Use only the libraries that provide immediate value for this challenge.

### [ ] US-02 Define UI contracts and state model

As a developer, I want a predictable screen contract so the UI remains easy to reason about and test.

Done when:

- [ ] the screen has a `UiState`
- [ ] user interactions are modeled as actions/events
- [ ] transient messages or retry prompts have a clear delivery mechanism

## E2. Random Weather Data

### [ ] US-03 Generate valid random coordinates

As a user, I want the app to use a valid random location so every refresh is meaningful and the request is always valid.

Done when:

- [ ] latitude is always within `[-90, 90]`
- [ ] longitude is always within `[-180, 180]`
- [ ] randomness is injectable or otherwise testable

### [ ] US-04 Fetch current weather for a random location

As a user, I want the app to automatically load current weather so I get useful content without extra steps.

Done when:

- [ ] the app fetches weather on first launch
- [ ] the request uses latitude and longitude against the current weather endpoint
- [ ] the request uses metric units
- [ ] transport models are mapped before they reach the UI

### [ ] US-05 Map API data into a UI-ready weather model

As a developer, I want a UI-focused model so formatting and fallback logic stay outside composables.

Done when:

- [ ] the mapped model contains all fields needed by the screen
- [ ] coordinates are formatted consistently
- [ ] wind speed is converted to km/h
- [ ] missing city names or optional API fields are handled gracefully

## E3. Screen UI

### [ ] US-06 Build the main weather screen

As a user, I want to see the weather in a clear and attractive layout so the information is easy to scan.

Done when:

- [ ] the screen includes app bar, hero header, summary card, metrics grid, lower context card, and refresh FAB
- [ ] the design follows the provided mock direction without unnecessary flourishes
- [ ] the layout works on typical phone sizes without clipping

### [ ] US-07 Support light and dark mode

As a user, I want the screen to look correct in light and dark themes so the app feels production-ready.

Done when:

- [ ] both themes are implemented
- [ ] colors, surfaces, and text contrast remain readable
- [ ] the app follows system theme or another clearly documented rule

### [ ] US-08 Define the lower context card

As a user, I want visual location context so the bottom section of the design feels intentional.

Done when:

- [ ] the section exists and fits the design hierarchy
- [ ] the card is visual only and does not add navigation or gestures in v1
- [ ] the implementation does not introduce unnecessary mapping SDK complexity
- [ ] the chosen approach is documented if it differs from a real map

## E4. States and Resilience

### [ ] US-09 Handle loading and refresh states

As a user, I want feedback during network activity so I understand when the app is working.

Done when:

- [ ] initial loading is visible
- [ ] refresh loading does not cause jarring UI flicker
- [ ] repeated taps do not trigger overlapping requests

### [ ] US-10 Handle recoverable failures

As a user, I want clear feedback and retry options when the request fails so the app remains usable.

Done when:

- [ ] failures are surfaced with a clear message
- [ ] retry is available
- [ ] previous successful content is preserved when appropriate

## E5. Testing and Quality

### [ ] US-11 Add unit tests for critical logic

As a reviewer, I need evidence that the important logic is correct and stable.

Done when:

- [ ] coordinate generation is tested
- [ ] ViewModel success, loading, refresh, and error paths are tested
- [ ] mapper logic is tested when non-trivial
- [ ] repository behavior is tested if it contains real coordination or translation logic

### [ ] US-12 Run validation commands before marking work complete

As a reviewer, I want relevant checks to pass so the submission is credible.

Done when:

- [ ] relevant unit tests were run
- [ ] lint was run if configured for the changed area
- [ ] the debug build assembles successfully
- [ ] any skipped validation is explicitly documented

Suggested commands:

- [ ] `./gradlew app:testDebugUnitTest`
- [ ] `./gradlew app:lintDebug`
- [ ] `./gradlew app:assembleDebug`
- [ ] `./gradlew app:connectedDebugAndroidTest` when UI/device coverage is added

## E6. Submission Readiness

### [ ] US-13 Prepare the README and final delivery notes

As a reviewer, I want clear documentation so I can understand the implementation and trade-offs quickly.

Done when:

- [ ] `README` explains architecture and package structure
- [ ] setup steps and API key handling are documented
- [ ] trade-offs and scope decisions are documented
- [ ] test commands and what was actually tested are documented
- [ ] AI usage is briefly disclosed

## Recommended Implementation Order

1. Complete E1 to lock the architecture and package layout.
2. Complete E2 so random coordinate generation and data retrieval work end-to-end.
3. Implement the success UI from E3 before polishing edge states.
4. Complete E4 to make loading, refresh, and errors robust.
5. Add and stabilize E5 tests before visual polish expands.
6. Finish E6 only once implementation and validation are stable.

## First Slice to Build

This is the smallest vertical slice worth implementing first:

1. Create the weather feature structure and `UiState`.
2. Add a testable random coordinate generator.
3. Call OpenWeather with metric units.
4. Map the response to a UI model with city fallback and km/h conversion.
5. Render a basic success screen with temperature, condition, coordinates, and refresh.
6. Add ViewModel tests for initial load, success, and refresh.

## Definition of Ready

Development can start once these decisions are accepted:

- [x] single-screen scope is confirmed
- [x] map is treated as a lightweight visual card, not a full map feature
- [x] UV index is replaced with `Local time`, derived from `dt` and `timezone`
- [x] metric units are the baseline
- [x] testing priority is ViewModel + coordinate generator + mapper/repository logic
