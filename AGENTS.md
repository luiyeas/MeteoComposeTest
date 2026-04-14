# Repository Guidelines

## Challenge Context

This repository is for an Android take-home challenge. Read `documentation/Instructions.md` before coding.

Key delivery constraints from the challenge and recruiter notes:

- Build a maintainable Android solution for a random-weather use case.
- The challenge allows the technical stack you are most comfortable with.
- The assessment explicitly values clean code, good practices, and SOLID principles.
- The solution must be clean, scalable, and mindful of performance.
- Testing is mandatory. The most important behaviors must be covered.
- A `README` must explain the solution, trade-offs, setup steps, and AI usage.

When the challenge defines a requirement, follow the challenge first. When it is silent, follow the defaults in this file.

## Delivery Priorities

Optimize for:

1. Correctness
2. Readability
3. Testability
4. Delivery speed
5. Scalability without over-engineering

Prefer the simplest production-ready implementation that can be defended in review. Do not add abstractions only for appearance.

## Project Baseline

The current repository is a single-module Android app:

- Root Gradle configuration: `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`
- Main app module: `app/`
- Kotlin source: `app/src/main/java/com/luisnavarro/fevertest`
- Resources: `app/src/main/res`
- Local unit tests: `app/src/test`
- Instrumentation tests: `app/src/androidTest`

Keep the project single-module unless the assessment explicitly grows beyond what a single module can support cleanly.

## Preferred Technical Direction

Use these defaults unless the repository or challenge requires a different choice:

- Kotlin
- Jetpack Compose for UI
- Material 3 components
- MVVM with unidirectional data flow
- Screen-level `ViewModel`
- `StateFlow` for persistent UI state
- `SharedFlow` only for one-off effects when they are actually needed
- `collectAsStateWithLifecycle()` in Compose screens
- Coroutines + Flow for asynchronous work
- Repository pattern for data access
- Retrofit + OkHttp for networking
- Dagger Hilt for dependency injection
- Constructor injection everywhere possible

For this repository, use Hilt as the default dependency injection framework. Keep the setup proportional to the app size, but do not fall back to a custom manual composition root unless the task explicitly requires something Hilt cannot handle cleanly.

## Architecture Rules

### UI and presentation

- Use MVVM by default.
- Each screen should have a screen-level `ViewModel`.
- Expose immutable screen state.
- Do not expose mutable flow types outside the `ViewModel`.
- UI sends actions upward; the `ViewModel` reduces state and coordinates work.
- Keep screen composables thin and child composables stateless where possible.
- Never pass a `ViewModel` into reusable child composables.
- Activities should act as app shells or navigation hosts only. Do not wire feature dependencies, feature state, or screen business logic in `MainActivity` unless the activity is itself the screen controller.

Preferred screen split:

- `FeatureRoute.kt`: obtains dependencies, collects state/effects, wires navigation
- `FeatureScreen.kt`: pure UI that receives `uiState` and callbacks
- `FeatureViewModel.kt`: state holder and business logic
- `FeatureUiState.kt`, `FeatureUiAction.kt`, `FeatureUiEffect.kt`: explicit screen contract when the feature complexity justifies it

For a very small feature, files can be combined if readability improves and responsibilities remain clear.

#### UI state modeling rules

- Avoid boolean-heavy `UiState` models that allow illegal or contradictory combinations.
- If loading, content, and error states are mutually exclusive, prefer a sealed render state or a similarly explicit model.
- Do not encode blocking-vs-inline error behavior through ad-hoc nullable field combinations when an explicit state model would be clearer.
- Keep user-facing copy out of the `ViewModel` when possible. Prefer error types, identifiers, or explicit presentation models that the UI can translate into strings.
- Use plain UI state holder classes for reusable UI component logic instead of introducing screen `ViewModel` dependencies.

### Data layer

- UI must not talk directly to Retrofit services, randomness utilities, or raw data sources.
- Route all data access through repositories or a similarly clear abstraction.
- Do not leak DTOs into the UI layer.
- Keep mapping explicit and easy to test.
- Treat random coordinate generation as business logic, not UI logic.
- The coordinate generator must produce valid latitude and longitude values and should be independently testable.
- Repositories should primarily coordinate data sources and expose app-facing models. When transport mapping, payload repair, error shaping, and policy decisions accumulate in one repository, split responsibilities into clearer collaborators such as remote data sources, mappers, or use cases.
- Do not put presentation formatting or user-facing strings in repositories.

### Dependency injection

- Use Hilt for dependency injection.
- Prefer constructor injection for `ViewModel`, repository, and use-case dependencies.
- Use Hilt modules only for bindings, Retrofit/OkHttp provisioning, dispatchers, API keys, and framework-provided objects.
- Do not manually instantiate repositories, Retrofit services, or `ViewModel` instances inside activities or composables.
- In Compose, obtain screen-level `ViewModel` instances with Hilt-aware APIs at the route level only.
- Default to unscoped bindings unless a shared lifetime is actually required.
- Scope a dependency only if it is expensive to create, holds shared mutable state, or must live as long as its DI container.
- Do not annotate simple value providers, pure factories, generators, or stateless helpers as `@Singleton` by default. Every scope should be easy to justify in review.

### Performance and scalability

- Do not trigger network calls from recomposition.
- Make refresh behavior explicit and deterministic.
- Avoid duplicate in-flight requests for the same user action.
- Prefer latest-request-wins behavior or disable refresh while loading.
- Keep expensive formatting and mapping out of composables.
- Handle loading, success, and error states explicitly.

## Working Mode

- Read the existing code before adding files.
- Keep diffs focused on the requested task.
- Reuse existing conventions when they are reasonable.
- Do not do unrelated refactors unless they are required to complete the task safely.
- If a requirement is ambiguous, choose the simplest reasonable interpretation and document it in the final summary and `README`.
- Write code and comments in English.
- Prefer expressive names over comments. Add comments only when they explain intent or a non-obvious trade-off.

## Testing Rules

Testing is non-negotiable for this challenge. Every feature, bug fix, or behavior change must include tests proportional to the change.

### Minimum testing bar

At least the following critical behaviors should be covered:

- random coordinate generation validity
- weather fetch success path
- loading and error state handling
- refresh behavior
- data mapping that is not trivial

### What to test first

#### 1. ViewModel tests

These are the default priority for this assessment. Cover, as relevant:

- initial state
- first load behavior
- success state reduction
- loading state visibility
- error state visibility
- refresh action
- prevention or handling of duplicate requests

#### 2. Repository tests

Add repository tests when the repository contains:

- DTO-to-domain or DTO-to-UI mapping
- error translation
- coordination between data sources
- caching or refresh decisions

#### 3. Randomness and mapper tests

These are especially important here. Test:

- latitude is always in `[-90, 90]`
- longitude is always in `[-180, 180]`
- generated values are reproducible when using an injected fake or seeded source
- mapping from weather response to app model behaves correctly for representative inputs

#### 4. UI or instrumentation tests

Add these only for the most critical user-facing flows if time allows, for example:

- refresh action is available
- key state is rendered for loading, success, or error

### Testing approach

- Prefer fakes over mocks when practical.
- Use constructor injection so dependencies are easy to replace.
- Use `runTest` for coroutine-based unit tests.
- Inject dispatchers instead of hardcoding them.
- Keep tests fast, deterministic, and isolated.
- Avoid relying on real network calls in automated tests.
- Do not add test-only runtime flags, system-property switches, or production branching whose only purpose is to support tests.
- Prefer test-only Hilt modules, fake bindings, or injected abstractions from `test` / `androidTest` sources instead.
- For UI tests, replace dependencies at the boundary; do not alter production behavior from the test runner.

Do not mark the task as finished if the main behavior is untested without a documented reason.

## Validation Workflow

Before finishing any task:

1. Confirm the implementation matches the requested behavior.
2. Run the narrowest relevant unit test command first.
3. Run broader checks if the change touches integration or Android-specific behavior.
4. Review the diff for unnecessary complexity, dead code, and naming issues.
5. Update `README` when assumptions, setup, or trade-offs changed.
6. Run a final polish pass focused on reviewer-facing details: unused controls, misleading affordances, naming consistency, dead code, preview realism, and test-only leftovers.

Preferred commands from the repository root:

```bash
./gradlew app:testDebugUnitTest
./gradlew app:lintDebug
./gradlew app:assembleDebug
./gradlew app:connectedDebugAndroidTest
```

Use the Gradle wrapper. Prefer the fastest command that gives meaningful validation for the change.

## README Requirements

The final `README` should include:

- architecture overview
- key technical decisions
- trade-offs and time-boxed decisions
- setup instructions, including API key handling
- commands to build and test
- assumptions or known limitations
- a brief note describing AI tool usage

## Configuration and Secrets

- Do not commit personal API keys in source files.
- Prefer `local.properties`, `gradle.properties`, or environment variables for secrets.
- If the provided OpenWeather key is used, treat it as challenge-supplied configuration, not as a pattern for future secrets handling.
- Keep machine-specific values out of versioned source where possible.

## Commit and Review Expectations

Local `.git` metadata is not available in this workspace, so repository-specific commit conventions cannot be inferred here. Use short, imperative commit messages such as `Add weather repository tests`.

Pull requests or final submissions should clearly state:

- what was implemented
- what was tested
- which commands were run
- which assumptions were made
- what remains intentionally out of scope

## Things to Avoid

- putting business logic in composables
- passing `ViewModel` to child composables
- exposing `MutableStateFlow` publicly
- triggering network requests directly from the UI layer
- leaking DTOs outside the data layer
- using real randomness directly inside UI tests
- introducing multi-module structure without a concrete need
- adding large libraries that do not materially improve the solution
- leaving key logic untested
- modeling exclusive screen states with loosely related booleans and nullable payloads when a clearer state model is available
- over-scoping Hilt dependencies just because they live in `SingletonComponent`
- mixing test-only logic into production code paths
- shipping interactive-looking UI elements with no behavior
