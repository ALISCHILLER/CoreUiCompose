package com.msa.coreui.calendar

import com.msa.coreui.calendar.model.PersianDateSelection

import com.msa.coreui.calendar.internal.indexFrom
import com.msa.coreui.calendar.internal.weekdayLabelOrder
import com.msa.coreui.calendar.internal.withDigitStyle
import com.msa.coreui.calendar.internal.ui.selectionSummary
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class PersianDateFormattingTest {
    @Test
    fun weekday_offsets_follow_configured_first_day() {
        assertEquals(0, DayOfWeek.MONDAY.indexFrom(DayOfWeek.MONDAY))
        assertEquals(6, DayOfWeek.SUNDAY.indexFrom(DayOfWeek.MONDAY))
        assertEquals(0, DayOfWeek.SATURDAY.indexFrom(DayOfWeek.SATURDAY))
    }

    @Test
    fun saturday_first_labels_are_reordered_without_changing_string_contract() {
        assertEquals(listOf(2, 3, 4, 5, 6, 0, 1), weekdayLabelOrder(DayOfWeek.MONDAY))
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6), weekdayLabelOrder(DayOfWeek.SATURDAY))
    }

    @Test
    fun ui_digit_conversion_changes_only_latin_digits() {
        assertEquals("۱۴۰۳/۰۱/۰۹", "1403/01/09".withDigitStyle(true))
        assertEquals("1403/01/09", "1403/01/09".withDigitStyle(false))
    }


    @Test
    fun field_selection_summary_respects_explicit_digit_style() {
        val strings = PersianDatePickerStrings.Persian.copy(defaultToPersianDigits = true)
        val dates = setOf(
            LocalDate.of(2024, 3, 20),
            LocalDate.of(2024, 3, 21),
        )

        assertEquals(
            "۲ تاریخ انتخاب شده",
            formatSelection(PersianDateSelection.Multiple(dates), strings, usePersianDigits = true),
        )
        assertEquals(
            "2 تاریخ انتخاب شده",
            formatSelection(PersianDateSelection.Multiple(dates), strings, usePersianDigits = false),
        )
    }

    @Test
    fun picker_selection_summary_handles_empty_open_and_complete_ranges() {
        val strings = PersianDatePickerStrings.English.copy(defaultToPersianDigits = false)
        val start = LocalDate.of(2024, 3, 20)
        val end = start.plusDays(1)

        assertEquals(
            strings.noSelection,
            selectionSummary(PersianDateSelection.Range(null, null), strings, usePersianDigits = false),
        )
        assertEquals(
            "1 Farvardin 1403",
            selectionSummary(PersianDateSelection.Range(start, null), strings, usePersianDigits = false),
        )
        assertEquals(
            "1 Farvardin 1403 to 2 Farvardin 1403",
            selectionSummary(PersianDateSelection.Range(start, end), strings, usePersianDigits = false),
        )
    }

    @Test
    fun field_selection_summary_formats_all_modes() {
        val strings = PersianDatePickerStrings.English.copy(defaultToPersianDigits = false)
        val date = LocalDate.of(2024, 3, 20)

        assertEquals(
            "1 Farvardin 1403",
            formatSelection(PersianDateSelection.Single(date), strings),
        )
        assertEquals(
            "1 Farvardin 1403 to 2 Farvardin 1403",
            formatSelection(PersianDateSelection.Range(date, date.plusDays(1)), strings),
        )
        assertEquals(
            strings.selectedCount(2),
            formatSelection(PersianDateSelection.Multiple(setOf(date, date.plusDays(1))), strings),
        )
        assertEquals("", formatSelection(PersianDateSelection.Single(null), strings))
    }

}
