# Calendar Data Contract

## Canonical representations

- `LocalDate`: persistence, API payload mapping, boundaries, disabled dates and picker selections.
- `PersianDate`: validated Solar Hijri year/month/day value and calendar arithmetic.
- `PersianYearMonth`: month navigation and month-length queries.
- `Clock`: resolving “today” only.
- Locale and digit style: display only.

Persian digits are never stored in selection models or returned as callback payloads.

## Conversion

```kotlin
val persian = PersianCalendar.fromGregorian(localDate)
val gregorian = PersianCalendar.toGregorian(persian)
```

Conversion is pure and timezone-independent. Do not convert through `Date`, `Calendar`, a fixed UTC offset or `HijrahChronology`.

## String formats

`PersianDateFormatter.parseIso` accepts normalized `yyyy-MM-dd` fields containing Latin, Persian or Arabic-Indic digits. Formatting methods are presentation helpers; transport contracts should be explicitly agreed with the backend.

## Supported range

The supported Persian years are `-61..3176`. Their exact Gregorian boundary is exposed as:

```kotlin
PersianCalendar.MIN_SUPPORTED_GREGORIAN_DATE // 0560-03-20
PersianCalendar.MAX_SUPPORTED_GREGORIAN_DATE // 3798-03-19
```

`fromGregorian` rejects input before integer day-number arithmetic when it is outside this boundary. Call `PersianCalendar.isSupported(localDate)` or `tryFromGregorian` for untrusted or externally supplied dates.

## Selection persistence

`PersianDatePickerState` saves Gregorian epoch days, the displayed Persian year/month and current picker view. Lambdas, context, colors and localized strings are not persisted.

## Breaking-change policy

Changing the conversion algorithm, supported range, leap-year behavior or serialized selection representation is a high-risk compatibility change. It requires independent reference fixtures, round-trip tests, migration notes and a major version when published behavior changes.
