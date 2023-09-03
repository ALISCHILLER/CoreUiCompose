@file:OptIn(ExperimentalAnimationGraphicsApi::class)
package com.msa.coreui.calendar.viewspersion

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msa.coreui.calendar.models.CalendarConfig
import com.msa.coreui.calendar.models.CalendarDisplayMode

@ExperimentalMaterial3Api
@Composable
internal fun CalendarTopComponentPersion(
    modifier: Modifier,
    config: CalendarConfig,
    mode: CalendarDisplayMode,
    navigationDisabled: Boolean,
    prevDisabled: Boolean,
    nextDisabled: Boolean,
    ){

}


@ExperimentalMaterial3Api
@Composable
@Preview
fun CalendarTopComponentPreview(){
   // CalendarTopComponentPersion()
}