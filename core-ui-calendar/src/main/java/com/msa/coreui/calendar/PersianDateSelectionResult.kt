package com.msa.coreui.calendar

import androidx.compose.runtime.Immutable
import com.msa.coreui.calendar.model.PersianDateSelection
import java.time.LocalDate

/** Stable rejection reasons produced by [PersianDatePickerState.selectDateResult]. */
enum class PersianDateSelectionRejection {
    OUT_OF_BOUNDS,
    DISABLED_DATE,
    DISABLED_RANGE,
    DISABLED_WEEKDAY,
    MULTIPLE_SELECTION_LIMIT,
    RANGE_CONTAINS_DISABLED_DATE,
    RANGE_TOO_SHORT,
    RANGE_TOO_LONG,
}


/** Explains whether the current selection can be confirmed without mutating picker state. */
enum class PersianDateConfirmationStatus {
    READY,
    EMPTY_SELECTION_ALLOWED,
    EMPTY_SELECTION_REQUIRED,
    RANGE_END_REQUIRED,
    MULTIPLE_SELECTION_BELOW_MINIMUM,
    NO_SELECTABLE_DATES,
}

/** Result of a date-selection interaction. */
sealed interface PersianDateSelectionResult {
    @Immutable
    data class Changed(
        val date: LocalDate,
        val selection: PersianDateSelection,
    ) : PersianDateSelectionResult

    @Immutable
    data class Rejected(
        val date: LocalDate,
        val reason: PersianDateSelectionRejection,
    ) : PersianDateSelectionResult

    data object Unchanged : PersianDateSelectionResult
}
