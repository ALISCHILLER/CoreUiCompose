# Architecture

## Project type

```text
Type: Android-only multi-module library
UI: Jetpack Compose / Material 3
Calendar domain: Pure JVM module
KMP targets: Not configured
Networking: Not applicable
Persistence: Not applicable
Dependency injection: Not required
```

Kotlin Multiplatform is intentionally not enabled. The pure `core-ui-calendar-core` boundary makes a future KMP migration possible without imposing platform complexity on the current Android library.

## Dependency direction

```text
catalog ───────────────┐
consumer-smoke ────────┼──────────────> core-ui (aggregate)
maven-consumer ─ Maven coordinates ───> build/repository/com.msa.coreui:core-ui
                                            │
                                            ├──api──> core-ui-foundation
                                            ├──api──> core-ui-components
                                            ├──api──> core-ui-calendar-core
                                            └──api──> core-ui-calendar

core-ui-components ─────────api────────> core-ui-foundation
core-ui-calendar ────────────api────────> core-ui-calendar-core
core-ui-calendar ────────────api────────> core-ui-foundation
core-ui-calendar ───implementation──────> core-ui-components
```

Rules:

1. Foundation does not depend on components or calendar.
2. Calendar-core has no Android or Compose dependency.
3. Calendar UI owns no conversion algorithm.
4. Application/catalog code does not enter public library modules.
5. The Maven consumer has no project dependency and verifies generated metadata.

## Public boundaries

- `core-ui-foundation`: design tokens, theme and adaptive layout.
- `core-ui-components`: reusable visual components and semantic state.
- `core-ui-calendar-core`: stable Persian date model and calendar utilities.
- `core-ui-calendar`: picker config, state, strings, colors and Compose UI.
- `core-ui`: aggregate convenience artifact only.

Packages containing `.internal` are not compatibility contracts. The conversion implementation is also declared with Kotlin `internal` visibility.

## Calendar data flow

```text
Gregorian LocalDate
        │
        ▼
PersianCalendar facade
        │
        ▼
internal deterministic engine
        │
        ▼
PersianDate / PersianYearMonth
```

- `LocalDate`: transport, persistence, boundary and selection value.
- `PersianDate`: Solar Hijri fields and arithmetic.
- `Clock`: resolves “today”; it does not participate in conversion.
- Locale/digit conversion: presentation only.

## Date-picker state

`PersianDatePickerState` is hoisted and is the single source of truth for:

- selection
- displayed year/month
- active days/months/years view
- boundary and disabled-date enforcement
- selection-mode limits

The saver stores epoch days, displayed year/month and view. Lambdas, contexts and UI objects are never persisted.

## Design-system flow

```text
CoreUiTheme
 ├── Material ColorScheme / Typography / Shapes
 ├── semantic status colors
 ├── spacing / radii / elevation
 ├── sizing / breakpoints
 ├── alpha / stroke / shared fallback strings
 └── motion policy
        │
        ▼
public components and calendar UI
```

## Compatibility policy

- Semantic versioning starts with the first stable 5.x publication.
- Public removals require a major release.
- Public renames require deprecation and migration guidance where practical.
- New optional parameters are appended with defaults.
- Public collections are immutable or defensively copied.
- Generated Maven metadata is tested by an independent consumer module.
