package com.msa.coreui.calendar

import com.msa.coreui.calendar.model.PersianDateSelection
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PersianDateSelectionFormatterTest {
    private val first = LocalDate.of(2025, 3, 21)
    private val second = LocalDate.of(2025, 3, 22)

    @Test
    fun emptySelectionUsesCallerFallback() {
        assertEquals(
            "No date",
            PersianDateSelectionFormatter.format(
                selection = PersianDateSelection.Single(null),
                strings = PersianDatePickerStrings.English,
                emptyText = "No date",
            ),
        )
    }

    @Test
    fun rangeIsLocalizedWithoutLeakingGregorianModelToString() {
        assertEquals(
            "1 Farvardin 1404 to 2 Farvardin 1404",
            PersianDateSelectionFormatter.format(
                selection = PersianDateSelection.Range(first, second),
                strings = PersianDatePickerStrings.English,
                usePersianDigits = false,
            ),
        )
    }

    @Test
    fun multipleSelectionRespectsPersianDigits() {
        assertEquals(
            "۲ تاریخ انتخاب شده",
            PersianDateSelectionFormatter.format(
                selection = PersianDateSelection.Multiple(setOf(first, second)),
                strings = PersianDatePickerStrings.Persian,
                usePersianDigits = true,
            ),
        )
    }
}
