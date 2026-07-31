# Changelog

## 5.7.0-SNAPSHOT

### Deep correctness and verification hardening

- Add real Navigation and responsive Layout catalog destinations instead of routing both home cards back to Home.
- Decouple primary navigation selection from `CatalogPage.ordinal` so detail pages cannot produce an invalid navigation index.
- Add interactive bottom-bar, rail, adaptive-grid, responsive-form and master-detail showcases with RTL-aware back navigation.
- Add Compose UI regression coverage for opening the Navigation and Layout destinations.
- Fix clean-checkout Maven consumer builds with Configuration Cache enabled: the consumer `preBuild` tasks now publish the local artifacts first and explicitly opt only those integration graphs out of Configuration Cache.
- Add a static regression gate for the published-consumer task wiring.
- Reject unsupported Gregorian dates before `Int` day-number arithmetic, preventing extreme-year overflow aliasing.
- Expose exact supported Gregorian boundary constants on `PersianCalendar`.
- Fix large-delta navigation when a clamped boundary month is fully disabled.
- Preserve explicitly allowed empty confirmation when no selectable dates exist.
- Short-circuit full-boundary disabled-date configurations to avoid unnecessary calendar scans.
- Add regression tests for overflow, boundary constants, navigation overshoot, empty confirmation and disabled-boundary behavior.
- Add a reproducible standalone calendar verifier and expand static/distribution auditing.

- Persist Catalog theme, language, digit, motion and accent preferences with defensive decoding.
- Add System/Light/Dark appearance and System/Persian/English language modes.
- Add adaptive Navigation Rail behavior for medium and expanded Catalog layouts.
- Restore navigation and form state across activity recreation and add Catalog restoration tests.
- Add a public localized `PersianDateSelectionFormatter` and verify it through project and Maven consumers.
- Add explicit backup/data-transfer exclusions, locale configuration and bilingual app-name resources.
- Run Catalog unit and instrumented tests from the project quality gates and emulator workflow.

## 5.6.0-SNAPSHOT

### Catalog redesign

- Rebuilt the catalog application to match the README showcase: purple gradient identity, card-based home, persistent four-destination navigation, Persian-first RTL presentation, inline Persian calendar, production-style form, feedback gallery, and live appearance settings.
- Added interactive light/dark mode, six selectable brand accents, Persian/Latin digits, RTL/LTR language switching, and reduced-motion preview.
- Changed the default Core UI brand palette from blue to the showcase purple while retaining injectable color schemes for consumers.
- Added a complete form workflow using CoreTextField, CorePasswordField, CoreSelectField and CoreMultiSelectField, plus success feedback and validation states.
- Added inline single/range/multiple Persian date selection with event indicators and confirmation feedback.
- Added stable catalog test tags and Compose UI navigation tests for the showcase landing, form flow, and live settings screen.

### Compatibility fixes

- Fixed Compose 2026 API compatibility across layout scope modifiers by removing imports of internal `weight` and `matchParentSize` symbols.
- Replaced unsupported vector path `cubicTo` calls with `curveTo`.
- Fixed missing `heightIn` import in chips and retained explicit `dp` imports for validation contracts.
- Moved theme/string reads outside non-composable semantics lambdas in expandable cards, select fields, progress, pagination, steppers, and the Persian date field.
- Added static audit guards preventing these regressions from returning.

## 5.5.1-SNAPSHOT

### Added

- Controlled slider, range-slider, pagination and adaptive-grid primitives with contract tests.
- Minimum range and multiple-selection requirements for the Persian date picker.
- Standalone Maven calendar consumer that validates transitive public API metadata.
- Localized number formatting for Persian pagination, steppers and character counters.

### Changed

- Calendar publication now exposes component types used by its public field API through Maven metadata.
- Modal sheets can block swipe, back and outside dismissal during protected workflows.
- Adaptive navigation validates compact item capacity before a width change can reach an invalid state.
- Select fields preserve stale selections safely without rendering raw model identifiers.
- Slider accessibility semantics are attached to the interactive control instead of a duplicate parent node.

### Fixed

- Replaced unsupported `Dp.Zero` references with the stable `0.dp` API across Foundation and Components.
- Prevented width-dependent adaptive-navigation crashes with more than five destinations.
- Prevented stale asynchronous select values from leaking `toString()` output into UI.
- Prevented duplicate accessibility focus for slider and range-slider labels.
- Added robust Calendar range/multiple confirmation constraints and restoration coverage.

## 5.3.0-SNAPSHOT

### Added

- Responsive navigation bar/rail scaffold, tabs with optional icons, modal bottom sheet, avatar, skeleton, floating action button, screen scaffold and accessible section header.
- Controlled `PersianDatePickerField` convenience component with a non-text Canvas affordance and restored dialog state.
- Component contract and Compose UI coverage for navigation, tabs, sheets, avatars and date-picker field behavior.
- `CoreUiAlpha`, `CoreUiStroke` and injectable `CoreUiStrings` token contracts.
- `PersianDatePickerDimensions` for adaptive picker sizing without implementation constants.
- Strict explicit-API compilation for the pure JVM calendar artifact.
- Static repository audit script, editor configuration, CODEOWNERS and PR quality template.
- Public API compatibility policy and deferred ABI-baseline requirement.
- `core-ui-calendar-core` pure JVM artifact.
- Strict Solar Hijri parsing for Latin, Persian and Arabic-Indic digits.
- Safe day/month/year and epoch-day arithmetic.
- Adaptive content container and window-size classes.
- Elevation, breakpoint, icon and dialog sizing tokens.
- Chips, switch/checkbox/radio rows and determinate/indeterminate progress components.
- Product-level reduced-motion policy with non-animated date-picker and loading paths.
- Configurable first day of week and adjacent-month navigation behavior.
- Optional empty-selection confirmation and disabled dates inside selected ranges.
- Aggregated Dokka documentation.
- CodeQL, dependency review and strengthened CI/release gates.
- Setup, dependency upgrade, accessibility and testing documentation.

### Changed

- Picker localization collections are defensively copied and all accessibility labels are validated.
- Calendar state restoration catches only expected invalid-state failures instead of swallowing fatal errors.
- The catalog disables backups and cleartext traffic.
- CI checkout steps no longer persist credentials and Gradle commands include stack traces.
- Kotlin `2.3.20` → `2.3.21`.
- Removed the unused Lifecycle Runtime Compose dependency.
- Calendar conversion implementation is no longer public API.
- Month/year selector grids are adaptive.
- Dialog sizing and elevations use shared tokens and caller-provided `DialogProperties`.
- Catalog demonstrates width classes, motion policy, RTL/LTR, Persian/English Calendar strings, new components and all selection modes.

### Compatibility

- Existing 5.3.0 snapshot entry points remain source-compatible unless consumers imported implementation details from `.internal` packages or destructured `PersianDatePickerStrings`; its validated `copy(...)` API remains available.
- `LoadingThreePoints` remains a deprecated alias.
- The original map-based prototype picker API is not part of the clean 5.x contract; migration is documented.
