package com.msa.coreuicompose

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.msa.coreui.calendar.PersianDatePicker

@Composable
fun MyCompose(){

    var hideDatePicker by remember {
        mutableStateOf(true)
    }


    Button(
        onClick = {hideDatePicker = false}
    ) {
        Text("انتخاب تاریخ")
    }

    if (!hideDatePicker){

        // *************************************************
        PersianDatePicker(
            onDismiss = { hideDatePicker = true },
            setDate = { date ->
                var day = date["day"]
                var month = date["month"]
                var year = date["year"]
            }
        )
        // *************************************************

    }

}