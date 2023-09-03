package com.msa.coreui.calendar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msa.corebase.views.base.FrameBase
import com.msa.coreui.calendar.models.CalendarConfig
import com.msa.coreui.calendar.models.CalendarDisplayMode
import com.msa.coreui.calendar.models.CalendarStyle
import com.msa.coreui.calendar.viewspersion.CalendarTopComponentPersion
import java.time.LocalDate

@ExperimentalMaterial3Api
@Composable
fun CalendarPersionView(){
    val disabledDates = listOf(
        LocalDate.now().minusDays(7),
        LocalDate.now().minusDays(12),
        LocalDate.now().plusDays(3),
    )


    val config=CalendarConfig(
        yearSelection = true,
        monthSelection = true,
        style = CalendarStyle.MONTH,
        disabledDates = disabledDates
    )
   // val calendarState = rememberCalendarState(selection, config)
    FrameBase(
        header = null,
        config =config ,
        layoutHorizontalAlignment = Alignment.CenterHorizontally,
        layout = {
//            CalendarTopComponentPersion(
//                modifier = Modifier.fillMaxWidth(),
//                config = config,
//                mode = calendarState.mode,
//                navigationDisabled = calendarState.mode != CalendarDisplayMode.CALENDAR,
//                prevDisabled = calendarState.isPrevDisabled,
//                nextDisabled = calendarState.isNextDisabled,
//            )
        }
    )
}




@Preview
@ExperimentalMaterial3Api
@Composable
fun CalendarPersionViewPreview(){
    CalendarPersionView()
}