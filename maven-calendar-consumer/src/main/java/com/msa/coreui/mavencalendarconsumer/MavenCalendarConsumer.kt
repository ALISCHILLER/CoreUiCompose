package com.msa.coreui.mavencalendarconsumer

import androidx.compose.runtime.Composable
import com.msa.coreui.calendar.PersianDatePickerField
import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.PersianDateSelectionFormatter
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.components.CoreFieldStatus
import java.time.LocalDate

/** Compile-only proof that the standalone calendar publication exposes its public dependencies. */
@Composable
fun MavenCalendarConsumer() {
    PersianDateSelectionFormatter.format(
        selection = PersianDateSelection.Single(LocalDate.of(2025, 3, 21)),
        strings = PersianDatePickerStrings.English,
        usePersianDigits = false,
    )
    PersianDatePickerField(
        selection = PersianDateSelection.Single(null),
        onSelectionChange = {},
        label = "Date",
        status = CoreFieldStatus.DEFAULT,
    )
}
