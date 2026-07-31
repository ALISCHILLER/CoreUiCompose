# Public API Overview

## Foundation

```text
CoreUiTheme
CoreUiLightColorScheme / CoreUiDarkColorScheme
CoreUiTypography / CoreUiShapes
CoreUiSpacing / CoreUiRadii / CoreUiElevation
CoreUiMotion / CoreUiSizing / CoreUiBreakpoints
CoreUiAlpha / CoreUiStroke / CoreUiStrings
CoreUiWindowSizeClass
CoreUiStatusColors
CoreContentContainer
```

## Components

```text
CoreButton / CoreButtonStyle / CoreButtonSize / CoreButtonContent
CoreIconButton / CoreIconButtonStyle
CoreTextField / CorePasswordField / CoreSelectField / CoreMultiSelectField / CoreFieldStatus
CoreCard / CoreStyledCard / CoreExpandableCard / CoreListItem
CoreChip / CoreInputChip / CoreChipStyle
CoreSwitchRow / CoreCheckboxRow / CoreRadioRow
CoreBanner / CoreBannerStyle
CoreSnackbar / CoreSnackbarContent
CoreLinearProgress / CoreCircularProgress / CoreSlider / CoreRangeSlider
CoreStateView / CoreLoadingIndicator
CoreAlertDialog / CoreContentDialog / CoreModalBottomSheet
CoreNavigationBar / CoreNavigationRail / CoreAdaptiveNavigationScaffold
CoreTabs / CoreSegmentedControl / CoreMultiSegmentedControl
CoreScreen / CoreScaffold / CoreTopBar / CoreFloatingActionButton
CoreAvatar / CoreBadge / CoreSkeleton / CoreAdaptiveGrid / CoreResponsivePane
CorePagination / CoreVerticalStepper / CoreValidationSummary / CoreFormSection
LoadingThreeDots
LoadingThreePoints (deprecated migration alias)
```

## Calendar-core

```text
PersianDate
PersianYearMonth
JalaliDate (Kotlin type alias)
PersianCalendar
PersianCalendar.MIN_SUPPORTED_GREGORIAN_DATE
PersianCalendar.MAX_SUPPORTED_GREGORIAN_DATE
PersianCalendarText
PersianDateFormatter
PersianDateSelection
PersianDateSelectionMode
```

## Calendar Compose API

```text
PersianDatePickerConfig
PersianDateDigitStyle
PersianDatePickerState
PersianDatePickerView
rememberPersianDatePickerState
PersianDatePicker
PersianDatePickerDialog
PersianDatePickerStrings
PersianDateSelectionFormatter
PersianDatePickerDimensions
PersianDatePickerColors
PersianDatePickerDefaults
```

Anything declared `internal` or placed under an `.internal` package is not a compatibility contract.
