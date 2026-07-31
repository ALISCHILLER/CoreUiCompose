# Source Integration Notes

## MSA Persian Calendar 4.0.0

The following concepts and source areas were retained and reorganized into `core-ui-calendar`:

- Borkowski/jalaali-js based conversion engine
- `PersianDate` and `PersianYearMonth`
- single, range and multiple selection models
- hoisted and saveable picker state
- Material 3 picker, dialog, strings and colors
- calendar engine, state and Compose UI tests

The public conversion facade, formatter, Core UI token integration, publication metadata and module boundary are new in this delivery.

## CoreUiCompose prototype and prior fixed delivery

The previous project was used as the compatibility and migration baseline. The deprecated `LoadingThreePoints` name is retained as a migration alias, and the prior dependency-exposure and calendar-correctness findings informed the new module boundaries.

Legacy `corebase`, ComposeDialogs-derived dialog implementations, duplicate calendar engines, event XML files and application-specific sample screens were intentionally not copied into the new public library. Pulling them into Foundation would recreate the original coupling and licensing surface.

## Product application code

The calendar application's repository, saved-date persistence, onboarding, settings and converter screens remain product-layer examples, not Core UI APIs. Their reusable UI requirements are represented in the catalog through cards, list rows, banners, states, fields and dialogs.
