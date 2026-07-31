package com.msa.coreui.calendar

import com.msa.coreui.calendar.model.GregorianDateRange
import com.msa.coreui.calendar.model.PersianDate
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.calendar.model.isComplete
import com.msa.coreui.calendar.model.isEmpty
import com.msa.coreui.calendar.model.selectedDateCount
import com.msa.coreui.calendar.model.toGregorianDateRangeOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class PersianCalendarPublicApiTest {
    @Test
    fun facade_converts_both_directions() {
        val gregorian = LocalDate.of(2024, 3, 20)
        val persian = PersianDate(1403, 1, 1)

        assertEquals(persian, PersianCalendar.fromGregorian(gregorian))
        assertEquals(gregorian, PersianCalendar.toGregorian(persian))
    }

    @Test
    fun formatter_supports_persian_and_latin_digits() {
        val date = PersianDate(1403, 1, 1)

        val persian = PersianDateFormatter.format(date, usePersianDigits = true)
        val latin = PersianDateFormatter.format(date, usePersianDigits = false)

        assertEquals("۱ فروردین ۱۴۰۳", persian)
        assertEquals("1 فروردین 1403", latin)
        assertTrue(PersianCalendar.isValid(1403, 12, 30))
    }

    @Test
    fun iso_parser_accepts_persian_and_arabic_indic_digits() {
        assertEquals(PersianDate(1403, 1, 9), PersianDateFormatter.parseIso("۱۴۰۳-۰۱-۰۹"))
        assertEquals(PersianDate(1403, 1, 9), PersianDateFormatter.parseIso("١٤٠٣-٠١-٠٩"))
        assertEquals(null, PersianDateFormatter.tryParseIso("1403-13-01"))
    }

    @Test
    fun arithmetic_clamps_day_at_shorter_month_and_leap_boundary() {
        assertEquals(PersianDate(1403, 7, 30), PersianDate(1403, 6, 31).plusMonths(1))
        assertEquals(PersianDate(1404, 12, 29), PersianDate(1403, 12, 30).plusYears(1))
        assertEquals(PersianDate(1404, 1, 1), PersianDate(1403, 12, 30).plusDays(1))
    }

    @Test
    fun epoch_day_and_days_until_are_consistent() {
        val start = PersianDate(1403, 1, 1)
        val end = start.plusDays(40)

        assertEquals(40L, start.daysUntil(end))
        assertEquals(start, PersianDate.fromEpochDay(start.toEpochDay()))
        assertEquals(end, PersianDate.fromEpochDay(end.toEpochDay()))
    }

    @Test
    fun numeric_formatter_supports_custom_separator_and_digit_style() {
        val date = PersianDate(1403, 9, 7)

        assertEquals("1403.09.07", PersianDateFormatter.numeric(date, separator = ".", usePersianDigits = false))
        assertEquals("۱۴۰۳/۰۹/۰۷", PersianDateFormatter.numeric(date))
    }

    @Test
    fun try_apis_return_null_only_for_invalid_inputs() {
        val minimum = PersianDate(PersianCalendar.MIN_SUPPORTED_YEAR, 1, 1).toGregorian()

        assertEquals(null, PersianCalendar.tryFromGregorian(minimum.minusDays(1)))
        assertEquals(null, PersianDate.tryParseIso("not-a-date"))
        assertEquals(null, PersianDateFormatter.tryParseIso("1403-13-01"))
        assertEquals(null, com.msa.coreui.calendar.model.PersianYearMonth.tryParseIso("1403-13"))
    }

    @Test
    fun selection_helpers_report_completion_without_expanding_ranges() {
        val start = LocalDate.of(2025, 1, 1)
        val end = LocalDate.of(2025, 1, 31)
        val empty = PersianDateSelection.Single(null)
        val range = PersianDateSelection.Range(start, end)

        assertTrue(empty.isEmpty)
        assertEquals(0, empty.selectedDateCount)
        assertTrue(range.isComplete)
        assertEquals(2, range.selectedDateCount)
        assertEquals(GregorianDateRange(start, end), range.toGregorianDateRangeOrNull())
    }

    @Test
    fun gregorian_date_range_is_inclusive_and_mergeable() {
        val first = GregorianDateRange(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 3),
        )
        val second = GregorianDateRange(
            LocalDate.of(2025, 1, 4),
            LocalDate.of(2025, 1, 8),
        )

        assertEquals(3L, first.dayCount)
        assertTrue(LocalDate.of(2025, 1, 2) in first)
        assertTrue(first.isAdjacentTo(second))
        assertEquals(
            GregorianDateRange(first.start, second.endInclusive),
            first.merge(second),
        )
    }

    @Test
    fun published_calendar_label_lists_are_unmodifiable() {
        assertThrows(UnsupportedOperationException::class.java) {
            @Suppress("UNCHECKED_CAST")
            (PersianCalendarText.PERSIAN_MONTH_NAMES as MutableList<String>)[0] = "changed"
        }
    }

}
