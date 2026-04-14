# Technical Review Audit

## Context

This audit translates the rejection feedback into concrete code-level findings and repository rules. Some feedback may refer to the submitted snapshot rather than the current working tree, so the focus here is:

- issues that are still visible in the codebase
- architectural patterns that should be avoided in future take-home submissions
- changes to `AGENTS.md` that raise the baseline for future implementations

## Main Findings

### 1. UI state is too permissive for a screen with exclusive states

Current state is modeled as multiple booleans plus nullable payloads in [WeatherUiState.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/main/java/com/luisnavarro/fevertest/feature/weather/WeatherUiState.kt#L3). This allows combinations such as:

- `isInitialLoading = true` and `errorMessage != null`
- `weather != null` and a blocking error derived from another field
- refresh and error semantics encoded through ad-hoc combinations

This is valid Kotlin, but it weakens scalability because the screen contract does not make illegal states impossible. For a senior-level take-home, a sealed `content/loading/error` render state or a more explicit state model would be easier to reason about and safer to evolve.

### 2. Repository boundary is doing more than raw data coordination

[WeatherRepository.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/main/java/com/luisnavarro/fevertest/data/weather/WeatherRepository.kt#L20) currently:

- owns the transport call
- injects the API key
- maps DTO to app model
- repairs missing coordinates with fallback request data
- converts wind units
- shapes malformed payload failures

None of these steps is wrong in isolation, but together they blur the repository boundary. For a small app this is acceptable, yet it becomes harder to scale because transport concerns, mapping rules, and data repair policies live in the same unit.

### 3. Scoping is broader than needed

[AppModule.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/main/java/com/luisnavarro/fevertest/di/AppModule.kt#L28) scopes almost everything as `@Singleton`, including:

- API key provider
- random location generator
- dispatchers
- repository binding

This is exactly the kind of over-scoping reviewers notice. In Hilt, defaulting to unscoped constructor injection is often cleaner unless the dependency is expensive, mutable, or intentionally shared for the whole app lifecycle.

### 4. Production code contains test-only runtime branching

[TestRuntime.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/main/java/com/luisnavarro/fevertest/core/testing/TestRuntime.kt#L1) is part of production code and is consulted from [WeatherComponents.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/main/java/com/luisnavarro/fevertest/feature/weather/WeatherComponents.kt#L456) to alter runtime behavior for tests. The test runner sets a system property in [FeverTestHiltTestRunner.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/androidTest/java/com/luisnavarro/fevertest/testing/FeverTestHiltTestRunner.kt#L15).

This solves a practical testing problem, but it weakens the architecture signal. Test setup should replace dependencies or inject alternate implementations from test sources, not add test flags into production execution paths.

### 5. There is still at least one visible polish issue

[WeatherComponents.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/main/java/com/luisnavarro/fevertest/feature/weather/WeatherComponents.kt#L91) renders a menu icon that looks actionable but has no behavior. This matches the feedback about small details and final polish. Senior-level submissions are usually expected to remove or justify every visible affordance.

### 6. ViewModel still owns some UI-specific presentation decisions

[WeatherViewModel.kt](/Users/lnavarro/Desktop/Projects/FeverTest/app/src/main/java/com/luisnavarro/fevertest/feature/weather/WeatherViewModel.kt#L86) converts failures directly into user-facing strings. The current app is small, so this is workable, but it couples the state holder to presentation copy and makes localization or richer error handling harder later.

## What To Improve Next Time

### Architecture

- Prefer screen state models that encode exclusivity explicitly.
- Keep activities as app shells or navigation hosts only.
- Keep repositories focused on coordination between data sources and domain mapping.
- Extract remote mapping or remote data source logic once a repository starts doing payload repair or transport shaping.

### Dependency injection

- Default to unscoped dependencies unless a shared lifetime is required.
- Scope only dependencies that are expensive, mutable, or intentionally reused.
- Treat immutable config values as plain provided values, not application-wide shared objects.

### Testing

- Do not add test-only flags or branches in production code.
- Prefer fake modules, fake composable slots, or injected abstractions from `androidTest`.
- Keep UI tests validating UI behavior, not implementation switches.

### Final polish

- Remove any element that appears interactive unless it has a real behavior.
- Run a final pass for naming consistency, dead code, previews, test-only leftovers, and copy.
- Review the submission from a reviewer perspective before sending it: architecture, scope, polish, and unnecessary cleverness.

## Standards To Bake Into AGENTS.md

The updated `AGENTS.md` should enforce:

- explicit guidance against boolean-heavy `UiState` models when states are mutually exclusive
- stricter repository boundaries
- "scope only when justified" for Hilt
- a ban on test-only runtime logic in production sources
- a final delivery polish checklist before considering the task complete

## Official References Used

- Android architecture recommendations: https://developer.android.com/topic/architecture/recommendations
- State holders and UI state: https://developer.android.com/topic/architecture/ui-layer/stateholders
- ViewModel overview and best practices: https://developer.android.com/topic/libraries/architecture/viewmodel
- Hilt testing guide: https://developer.android.com/training/dependency-injection/hilt-testing
- Dagger scoping best practices: https://developer.android.com/training/dependency-injection/dagger-android
