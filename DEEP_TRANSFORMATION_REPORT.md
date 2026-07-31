# Deep Transformation Report

Date: 2026-07-27

## Executive status

MSA Core UI is now a modular, production-oriented Android Jetpack Compose library. The pure calendar engine, design foundation, reusable components, Calendar Compose UI, aggregate artifact, catalog and two consumer-verification paths have separate responsibilities.

The delivery is **statically hardened and partially JVM-verified**. It is not labeled Production-Ready because Android dependency resolution, debug/release builds, lint, connected UI tests, generated Maven publication and runtime smoke testing could not execute in the delivery environment.

## Phase 0 — Discovery and evidence

### Scope

- Compared the prior professional delivery with the imported MSA Persian Calendar source.
- Classified the project as Android-only with one pure JVM calendar module.
- Reviewed Gradle, public API, Compose state, localization, accessibility, security, publishing and CI.

### Exit gate

Passed for static discovery.

## Phase 1 — Build and dependency resolution

### Changes

- Centralized versions in `gradle/libs.versions.toml`.
- Retained JDK 17, Gradle 8.13 and AGP 8.13.2.
- Applied the compatible Kotlin patch update to 2.3.21.
- Removed an unused lifecycle dependency.
- Kept Compose on BOM 2026.06.00 and Material 3 only.
- Added wrapper distribution checksum, local publication repository and independent Maven consumer.

### Verification

- Gradle wrapper JAR integrity: passed.
- Wrapper distribution download: blocked by DNS/network.
- Dependency resolution and Android build: not executed.

### Rollback

Revert `gradle/libs.versions.toml`, `gradle.properties` and the wrapper properties as one toolchain batch.

## Phase 2 — Calendar-core isolation

### Changes

- Created `core-ui-calendar-core` as a pure JVM artifact.
- Moved conversion, validation, parsing, formatting and arithmetic out of Compose UI.
- Exposed `PersianCalendar`, `PersianDate`, `PersianYearMonth`, `PersianDateFormatter` and selection models.
- Kept the conversion implementation internal.
- Enabled Kotlin explicit API mode and warnings-as-errors.
- Defensively copied public label and selection collections.

### Verification

- Strict explicit-API JVM compilation: passed.
- Known anchors and arithmetic checks: passed.
- Every supported Persian date round-trip: passed, 1,182,655 dates.

### Rollback

The module can be reverted independently while keeping UI consumers on the previous artifact, provided the aggregate dependency map is reverted in the same change.

## Phase 3 — State and calendar UI

### Changes

- Kept selection state hoisted and saveable.
- Added single, range and multiple selection rules.
- Added boundaries, disabled dates, multiple-selection limits and range policy.
- Added injectable `Clock`, digit style, first weekday and adjacent-month policy.
- Added customizable `PersianDatePickerDimensions`.
- Made localization collections immutable through defensive copies.
- Restricted saver recovery to expected invalid-state exceptions.
- Added reduced-motion and adaptive month/year rendering paths.

### Verification

- Standalone state-machine verifier: passed.
- Localization/token/dimension contract type-check: passed.
- Gradle local and Compose UI tests: configured, not executed.

### Rollback

Keep the old public state/config call sites behind a release branch, or revert the calendar UI module and aggregate dependency together. Persisted values remain Gregorian epoch days, so no user data migration is required.

## Phase 4 — Design foundation

### Changes

- Central Material 3 theme with light/dark and optional dynamic color.
- Semantic success, warning and info colors.
- Spacing, radius, elevation, alpha, stroke, motion, sizing and breakpoint tokens.
- Injectable shared fallback strings.
- Adaptive `CoreContentContainer`.

### Verification

- Token validation tests added.
- Standalone token/string model type-check: passed.
- Android theme runtime: not executed.

### Rollback

Token families are independently injectable. Revert only the new token parameter and component usage in the same commit if a consumer incompatibility is found.

## Phase 5 — Components and accessibility

### Changes

- Standardized buttons, icon buttons, fields, cards, list rows, chips, selection controls, dialogs, feedback, snackbar, progress and loading.
- Added non-blank contract validation for labels and paired action callbacks.
- Added selected, checked, disabled, error, progress and loading semantics.
- Added minimum touch targets and reduced-motion loading behavior.
- Retained `LoadingThreePoints` as a deprecated migration alias.

### Verification

- Compose UI semantics tests added.
- Runtime TalkBack/Accessibility Scanner verification: not executed.

### Rollback

Consumers can remain on the previous snapshot or migrate component-by-component. The deprecated loading alias provides a source-compatible bridge.

## Phase 6 — Publishing and consumers

### Changes

- Separate Maven publications for foundation, components, calendar-core, calendar UI and aggregate artifact.
- Sources artifacts and POM license/developer/SCM metadata.
- Project-dependency consumer and Maven-coordinate consumer.
- Aggregated Dokka configuration.

### Verification

- Publication configuration: statically reviewed.
- POM generation and external resolution: not executed.

### Rollback

No remote publication is performed automatically. Delete the local `build/repository` output and revert publication blocks if metadata validation fails.

## Phase 7 — Security and supply chain

### Changes

- Secret/signing file ignores.
- Static secret and risky-source scan.
- Catalog disables backup and cleartext traffic.
- CI checkout does not persist credentials.
- Least-privilege workflow permissions.
- CodeQL and dependency review workflows.
- Dependabot grouping for toolchain, Compose, tests and actions.

### Verification

- Static secret-pattern scan: passed.
- Workflow YAML parsing: passed.
- CodeQL/dependency review execution: not executed.

### Rollback

Security controls should not be rolled back as a group. Revert only a failing workflow version while retaining permissions and credential handling.

## Phase 8 — Documentation and release control

### Changes

- README, Setup, API, architecture, design system, accessibility, testing, migration, security, changelog and release checklist.
- Public API compatibility policy.
- Static audit script, EditorConfig, CODEOWNERS and PR template.
- Production wording corrected to `production-oriented` / `build verification required`.

### Exit gate

Documentation coverage passed static review. Runtime claims remain explicitly unverified.

## Phase 9 - Deep correctness and evidence hardening (2026-07-30)

### Scope

- Re-audited the delivered source against the evidence-first Android/Compose/KMP master prompt.
- Reproduced calendar-core and date-picker state tests without relying on an Android SDK.
- Probed unsupported Gregorian years, disabled-date boundary behavior and large-delta month navigation.
- Reconciled documentation claims with checks that are actually shipped and executable.

### Findings and changes

1. **High - Gregorian overflow aliasing:** the conversion engine used `Int` arithmetic before rejecting unsupported dates. Some extreme `LocalDate` years overflowed into an apparently supported Persian date. Conversion now validates the exact Gregorian boundary (`0560-03-20` through `3798-03-19`) before arithmetic. Public boundary values were added for consumers.
2. **Medium - navigation overshoot:** a large positive/negative month delta could clamp onto a disabled edge and return `false` even when a selectable month remained between the edge and current month. Navigation now uses a constrained fallback without reversing direction past the current month.
3. **Medium - empty confirmation precedence:** explicitly permitted empty confirmation now remains available even if the configured boundary contains no selectable dates.
4. **Medium - disabled-range initialization cost:** all-weekday-disabled and explicitly fully-disabled boundaries now short-circuit month/year/date scans. A local full-range probe improved initialization from approximately 147 ms to approximately 19 ms; Android profiling is still required.
5. **Medium - verification drift:** static-audit documentation previously exceeded the checks implemented by the script. The audit now validates scripts, XML/TOML/JSON/YAML, trailing whitespace, wrapper integrity/checksum, manifests, workflow security baseline, module shape, localization parity and high-confidence secret patterns. A standalone calendar verification script now ships with the project.

### Verification

- Static audit: passed.
- Distribution gate: passed.
- Calendar-core strict explicit API compile: passed.
- Calendar-core tests: 21 passed.
- Date-picker state/localization/contract tests: 49 passed.
- Full supported Persian range: 1,182,655 dates round-tripped.
- Gradle/Android build: blocked by unavailable network distribution and Android SDK.

### Compatibility

- Changes are source-compatible additions/fixes within the `5.7.0-SNAPSHOT` line.
- Inputs outside the documented Gregorian range now fail deterministically instead of being vulnerable to overflow aliasing. Any consumer that depended on conversion of unsupported extreme years was relying on invalid behavior.
- Persisted selection representation remains Gregorian epoch days; no data migration is required.

### Rollback

- Calendar boundary validation and public constants can be reverted as one core patch, but doing so would reintroduce overflow aliasing.
- Date-picker state changes and their tests can be reverted independently from the core engine.
- Audit/verifier scripts are isolated from runtime artifacts and can be reverted without changing library behavior.
