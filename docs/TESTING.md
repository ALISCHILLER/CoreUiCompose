# Testing Strategy

## `core-ui-calendar-core`

JVM tests cover:

- known Gregorian/Solar Hijri anchors
- leap and non-leap Esfand
- every Gregorian day in the product range
- every Persian day in the engine-supported range
- round-trip conversion
- month/year/day arithmetic
- parsing Persian, Arabic-Indic and Latin digits
- supported-range boundaries, extreme Gregorian overflow aliasing and fail-fast rejection

## `core-ui-calendar`

Local unit tests cover:

- single, range and multiple selection
- disabled dates
- range normalization
- optional disabled dates inside a range
- empty-selection confirmation policy
- multiple-selection limits
- boundary month/year clamping and large-delta disabled-month navigation
- complete-boundary disabled-date short-circuits
- defensive collection copying
- localization contract validation and immutable custom label collections
- picker-dimension validation

Compose UI tests cover:

- minimum supported month rendering
- hoisted selection updates
- indicator semantics
- configurable first weekday

## `core-ui-components`

Compose UI tests cover:

- loading/enabled button semantics
- required icon-button label
- selected chip state
- determinate progress label
- reduced-motion loading semantics

## Consumer verification

- `consumer-smoke` compiles using only `project(":core-ui")`.
- `maven-consumer` compiles using only generated Maven coordinates.
- The latter must run after publication to `build/repository`.

## Required release matrix

```text
JVM calendar tests
Android local unit tests
Library lintRelease
Catalog debug and minified release assembly
Consumer project assembly
Generated Maven consumer assembly
Dokka generation
Connected Compose UI tests
Manual catalog smoke test
```

Tests must use injected clocks and deterministic dates. A test file existing in the repository is not equivalent to a passing test run.

## Standalone verifier

`scripts/verify-calendar-standalone.sh` compiles the pure calendar module with strict explicit API and executes the calendar-core and non-rendering date-picker state/formatting/contract tests using minimal Compose type stubs. It is a fallback evidence path when an Android SDK is unavailable; it does not verify Android resources, Compose rendering, lint, R8, publication or emulator behavior.
