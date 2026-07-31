# Migration Guide

## 5.7.0 correctness notes

- `PersianCalendar.MIN_SUPPORTED_GREGORIAN_DATE` and `MAX_SUPPORTED_GREGORIAN_DATE` are additive public APIs.
- Gregorian dates outside `0560-03-20..3798-03-19` are now rejected before conversion. Previous apparent results for extreme years were overflow aliases and were never a supported contract.
- `allowEmptySelectionConfirmation=true` now consistently permits confirming an empty selection, including a boundary with no selectable dates.
- Large month-navigation deltas may now reach the nearest selectable month in the requested direction instead of returning `false` at a disabled clamped edge.


## From the original CoreUiCompose prototype

### Module dependency

Before:

```kotlin
implementation(project(":CoreUi"))
implementation(project(":corebase"))
```

After:

```kotlin
implementation("com.msa.coreui:core-ui:5.7.0-SNAPSHOT")
```

The aggregate artifact exposes foundation, components and calendar. Applications no longer depend on internal base modules.

### Untyped date callback

Before:

```kotlin
PersianDatePicker(
    onDismiss = { dismissed -> },
    setDate = { map ->
        val year = map["year"]
    },
)
```

After:

```kotlin
val state = rememberPersianDatePickerState()

PersianDatePickerDialog(
    state = state,
    onDismissRequest = { showPicker = false },
    onConfirm = { selection ->
        val date = (selection as PersianDateSelection.Single).date
    },
)
```

### Loading

`LoadingThreePoints` remains as a deprecated compatibility alias. Migrate to:

```kotlin
LoadingThreeDots(contentDescription = "Loading results")
```

## From MSA Persian Calendar 4.0.0

The picker APIs keep the same package and core state model. The library has moved from the monolithic `core-ui` module to `core-ui-calendar`; the aggregate `core-ui` dependency remains the easiest installation path.

New public utility APIs:

```kotlin
PersianCalendar.fromGregorian(date)
PersianCalendar.toGregorian(persianDate)
PersianDateFormatter.format(persianDate)
```

Calendar colors continue to derive from `MaterialTheme`; wrap the application in `CoreUiTheme` to use the shared design tokens.

## Snapshot localization model hardening

`PersianDatePickerStrings` now defensively copies custom month and weekday lists. Its validated `copy(...)` API remains available. Data-class destructuring was never a documented compatibility contract and is no longer generated; replace destructuring with named properties.
