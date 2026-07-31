package com.msa.coreui.calendar

import androidx.compose.runtime.Immutable
import com.msa.coreui.calendar.model.GregorianDateRange
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Collections
import java.util.EnumSet
import java.util.TreeSet

enum class PersianDateDigitStyle {
    AUTO,
    PERSIAN,
    LATIN,
}

/** Immutable behavioral configuration for [PersianDatePicker]. */
@Immutable
class PersianDatePickerConfig(
    val minDate: LocalDate = LocalDate.of(1900, 1, 1),
    val maxDate: LocalDate = LocalDate.of(2100, 12, 31),
    disabledDates: Set<LocalDate> = emptySet(),
    disabledDateRanges: List<GregorianDateRange> = emptyList(),
    disabledWeekdays: Set<DayOfWeek> = emptySet(),
    val minMultipleSelection: Int = 0,
    val maxMultipleSelection: Int? = null,
    val minRangeDays: Int = 1,
    val maxRangeDays: Int? = null,
    val showAdjacentMonthDays: Boolean = true,
    val autoNavigateAdjacentMonth: Boolean = true,
    val firstDayOfWeek: DayOfWeek = DayOfWeek.SATURDAY,
    val allowDisabledDatesInsideRange: Boolean = false,
    val allowEmptySelectionConfirmation: Boolean = false,
    val digitStyle: PersianDateDigitStyle = PersianDateDigitStyle.AUTO,
    val showTodayAction: Boolean = true,
    val skipFullyDisabledMonths: Boolean = true,
    val enableSwipeNavigation: Boolean = true,
) {
    val disabledDates: Set<LocalDate>
    val disabledDateRanges: List<GregorianDateRange>
    val disabledWeekdays: Set<DayOfWeek>

    init {
        require(!minDate.isAfter(maxDate)) { "minDate must be on or before maxDate." }
        require(minMultipleSelection >= 0) { "minMultipleSelection must not be negative." }
        require(maxMultipleSelection == null || maxMultipleSelection > 0) {
            "maxMultipleSelection must be null or greater than zero."
        }
        require(maxMultipleSelection == null || minMultipleSelection <= maxMultipleSelection) {
            "minMultipleSelection must not exceed maxMultipleSelection."
        }
        require(minRangeDays > 0) { "minRangeDays must be greater than zero." }
        require(maxRangeDays == null || maxRangeDays >= minRangeDays) {
            "maxRangeDays must be null or greater than or equal to minRangeDays."
        }
        val boundaryDayCount = ChronoUnit.DAYS.between(minDate, maxDate) + 1L
        require(minMultipleSelection.toLong() <= boundaryDayCount) {
            "minMultipleSelection must not exceed the configured date boundary."
        }
        require(maxMultipleSelection == null || maxMultipleSelection.toLong() <= boundaryDayCount) {
            "maxMultipleSelection must not exceed the configured date boundary."
        }
        require(minRangeDays.toLong() <= boundaryDayCount) {
            "minRangeDays must not exceed the configured date boundary."
        }
        require(maxRangeDays == null || maxRangeDays.toLong() <= boundaryDayCount) {
            "maxRangeDays must not exceed the configured date boundary."
        }
        require(PersianCalendar.isSupported(minDate)) {
            "minDate is outside the supported Persian calendar range."
        }
        require(PersianCalendar.isSupported(maxDate)) {
            "maxDate is outside the supported Persian calendar range."
        }

        this.disabledDates = Collections.unmodifiableSet(
            TreeSet(disabledDates.filter { date -> date in minDate..maxDate }),
        )
        this.disabledDateRanges = Collections.unmodifiableList(
            normalizeRanges(
                disabledDateRanges.mapNotNull { range ->
                    val start = maxOf(range.start, minDate)
                    val end = minOf(range.endInclusive, maxDate)
                    if (start <= end) GregorianDateRange(start, end) else null
                },
            ),
        )
        this.disabledWeekdays = Collections.unmodifiableSet(
            if (disabledWeekdays.isEmpty()) {
                EnumSet.noneOf(DayOfWeek::class.java)
            } else {
                EnumSet.copyOf(disabledWeekdays)
            },
        )
    }

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is PersianDatePickerConfig &&
            minDate == other.minDate &&
            maxDate == other.maxDate &&
            disabledDates == other.disabledDates &&
            disabledDateRanges == other.disabledDateRanges &&
            disabledWeekdays == other.disabledWeekdays &&
            minMultipleSelection == other.minMultipleSelection &&
            maxMultipleSelection == other.maxMultipleSelection &&
            minRangeDays == other.minRangeDays &&
            maxRangeDays == other.maxRangeDays &&
            showAdjacentMonthDays == other.showAdjacentMonthDays &&
            autoNavigateAdjacentMonth == other.autoNavigateAdjacentMonth &&
            firstDayOfWeek == other.firstDayOfWeek &&
            allowDisabledDatesInsideRange == other.allowDisabledDatesInsideRange &&
            allowEmptySelectionConfirmation == other.allowEmptySelectionConfirmation &&
            digitStyle == other.digitStyle &&
            showTodayAction == other.showTodayAction &&
            skipFullyDisabledMonths == other.skipFullyDisabledMonths &&
            enableSwipeNavigation == other.enableSwipeNavigation

    override fun hashCode(): Int {
        var result = minDate.hashCode()
        result = 31 * result + maxDate.hashCode()
        result = 31 * result + disabledDates.hashCode()
        result = 31 * result + disabledDateRanges.hashCode()
        result = 31 * result + disabledWeekdays.hashCode()
        result = 31 * result + minMultipleSelection
        result = 31 * result + (maxMultipleSelection ?: 0)
        result = 31 * result + minRangeDays
        result = 31 * result + (maxRangeDays ?: 0)
        result = 31 * result + showAdjacentMonthDays.hashCode()
        result = 31 * result + autoNavigateAdjacentMonth.hashCode()
        result = 31 * result + firstDayOfWeek.hashCode()
        result = 31 * result + allowDisabledDatesInsideRange.hashCode()
        result = 31 * result + allowEmptySelectionConfirmation.hashCode()
        result = 31 * result + digitStyle.hashCode()
        result = 31 * result + showTodayAction.hashCode()
        result = 31 * result + skipFullyDisabledMonths.hashCode()
        result = 31 * result + enableSwipeNavigation.hashCode()
        return result
    }

    override fun toString(): String =
        "PersianDatePickerConfig(" +
            "minDate=$minDate, " +
            "maxDate=$maxDate, " +
            "disabledDates=$disabledDates, " +
            "disabledDateRanges=$disabledDateRanges, " +
            "disabledWeekdays=$disabledWeekdays, " +
            "minMultipleSelection=$minMultipleSelection, " +
            "maxMultipleSelection=$maxMultipleSelection, " +
            "minRangeDays=$minRangeDays, " +
            "maxRangeDays=$maxRangeDays, " +
            "showAdjacentMonthDays=$showAdjacentMonthDays, " +
            "autoNavigateAdjacentMonth=$autoNavigateAdjacentMonth, " +
            "firstDayOfWeek=$firstDayOfWeek, " +
            "allowDisabledDatesInsideRange=$allowDisabledDatesInsideRange, " +
            "allowEmptySelectionConfirmation=$allowEmptySelectionConfirmation, " +
            "digitStyle=$digitStyle, " +
            "showTodayAction=$showTodayAction, " +
            "skipFullyDisabledMonths=$skipFullyDisabledMonths, " +
            "enableSwipeNavigation=$enableSwipeNavigation)"

    private fun normalizeRanges(
        ranges: List<GregorianDateRange>,
    ): List<GregorianDateRange> {
        if (ranges.isEmpty()) return emptyList()

        val sorted = ranges.sortedWith(
            compareBy<GregorianDateRange> { it.start }.thenBy { it.endInclusive },
        )
        val normalized = ArrayList<GregorianDateRange>(sorted.size)
        sorted.forEach { range ->
            val previous = normalized.lastOrNull()
            if (previous != null && (previous.overlaps(range) || previous.isAdjacentTo(range))) {
                normalized[normalized.lastIndex] = previous.merge(range)
            } else {
                normalized += range
            }
        }
        return normalized
    }
}
