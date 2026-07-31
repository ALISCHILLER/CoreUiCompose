package com.msa.coreui.calendar

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PersianDatePickerContractTest {
    @Test
    fun strings_defensively_copy_custom_collections() {
        val months = PersianDatePickerStrings.English.monthNames.toMutableList()
        val shortDays = PersianDatePickerStrings.English.shortWeekdayNames.toMutableList()
        val fullDays = PersianDatePickerStrings.English.weekdayNames.toMutableList()
        val strings = englishStrings(
            monthNames = months,
            shortWeekdayNames = shortDays,
            weekdayNames = fullDays,
        )

        months[0] = "Changed"
        shortDays.clear()
        fullDays.clear()

        assertEquals("Farvardin", strings.monthNames.first())
        assertEquals(7, strings.shortWeekdayNames.size)
        assertEquals(7, strings.weekdayNames.size)
        assertThrows(UnsupportedOperationException::class.java) {
            @Suppress("UNCHECKED_CAST")
            (strings.monthNames as MutableList<String>).add("Extra")
        }
    }

    @Test
    fun strings_copy_revalidates_and_defensively_copies() {
        val mutableMonths = PersianDatePickerStrings.English.monthNames.toMutableList()
        val copied = PersianDatePickerStrings.English.copy(monthNames = mutableMonths)

        mutableMonths[0] = "Changed"

        assertEquals("Farvardin", copied.monthNames.first())
    }

    @Test
    fun strings_reject_blank_accessibility_contracts() {
        assertThrows(IllegalArgumentException::class.java) {
            englishStrings(title = " ")
        }
        assertThrows(IllegalArgumentException::class.java) {
            englishStrings(selectedCount = { "" })
        }
        assertThrows(IllegalArgumentException::class.java) {
            englishStrings(indicatorCountLabel = { "" })
        }
        assertThrows(IllegalArgumentException::class.java) {
            englishStrings(disabledRangeLabel = " ")
        }
    }

    @Test
    fun dimensions_reject_invalid_values() {
        assertThrows(IllegalArgumentException::class.java) {
            PersianDatePickerDimensions(indicatorSize = 0.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            PersianDatePickerDimensions(dayContentSize = 2.dp, indicatorSize = 4.dp)
        }
    }

    private fun englishStrings(
        title: String = "Select date",
        monthNames: List<String> = PersianDatePickerStrings.English.monthNames,
        shortWeekdayNames: List<String> = PersianDatePickerStrings.English.shortWeekdayNames,
        weekdayNames: List<String> = PersianDatePickerStrings.English.weekdayNames,
        selectedCount: (Int) -> String = { "$it selected" },
        indicatorCountLabel: (Int) -> String = { "$it indicators" },
        disabledRangeLabel: String = "Inside disabled range",
    ): PersianDatePickerStrings = PersianDatePickerStrings(
        title = title,
        cancel = "Cancel",
        confirm = "Confirm",
        today = "Today",
        clear = "Clear",
        previousMonth = "Previous month",
        nextMonth = "Next month",
        selectMonth = "Select month",
        selectYear = "Select year",
        singleSelectionHint = "Choose one date",
        rangeSelectionHint = "Choose a range",
        multipleSelectionHint = "Choose dates",
        noSelection = "No selection",
        selectedLabel = "Selected",
        rangeStartLabel = "Range start",
        rangeEndLabel = "Range end",
        inRangeLabel = "In range",
        unavailableLabel = "Unavailable",
        outOfBoundsLabel = "Outside allowed range",
        disabledDateLabel = "Disabled date",
        disabledRangeLabel = disabledRangeLabel,
        disabledWeekdayLabel = "Disabled weekday",
        selectionLimitReachedLabel = "Selection limit reached",
        selectedCount = selectedCount,
        rangeSeparator = "to",
        defaultToPersianDigits = false,
        monthNames = monthNames,
        shortWeekdayNames = shortWeekdayNames,
        weekdayNames = weekdayNames,
        adjacentMonthLabel = "Adjacent month",
        indicatorCountLabel = indicatorCountLabel,
    )
}
