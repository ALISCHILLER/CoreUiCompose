package com.msa.coreui.calendar

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import com.msa.coreui.calendar.utils.DataPersion.JalaliCalendarD


import kotlinx.coroutines.launch
import java.time.Year

@Composable
fun PersianDatePicker(
    onDismiss : (Boolean) -> Unit,
    minYear: Int = 1350,
    maxYear: Int = 1420,
    positiveButtonTxt : String = "تایید",
    negativeButtonTxt : String = "لغو",
    shortWeekDays: Boolean = false,
    setDate : (Map<String, String>) -> Unit
){

    val today = JalaliCalendarD().day
    val month = JalaliCalendarD().month
    val year = JalaliCalendarD().year

    val monthsList = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد",
        "شهریور","مهر","آبان","آذر","دی","بهمن","اسفند",)

    var selectedPart by remember {
        mutableStateOf("main")
    }

    var mMonth by remember {
        mutableStateOf(monthsList[month - 1])
    }

    var mYear by remember {
        mutableStateOf(year.toString())
    }

    var mDay by remember {
        mutableStateOf(today.toString())
    }

    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp * .6

    Dialog(
        onDismissRequest = { onDismiss(true) }
    ) {
        Card(
            modifier = Modifier
                .size(width = width.dp, height = 530.dp)
                .padding(5.dp),
            shape = RoundedCornerShape(10.dp),
            elevation =CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            colors=CardDefaults.cardColors(
                containerColor= MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainContent(
                    mMonth, mYear, mDay,
                    minYear = minYear ,
                    maxYear = maxYear ,
                    shortWeekDays = shortWeekDays,
                    selectedPart,{mDay = it},
                    setMonth = {mMonth = it},
                    setYear = {mYear = it} ){selectedPart = it}

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = negativeButtonTxt,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clickable {
                                onDismiss(true)
                            }
                            .padding(horizontal = 10.dp)
                    )
                    Text(
                        text = positiveButtonTxt,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .clickable {
                                onDismiss(true)
                                setDate(
                                    mapOf(
                                        "day" to mDay,
                                        "month" to (monthsList.indexOf(mMonth) + 1).toString(),
                                        "year" to mYear
                                    )
                                )
                            }
                            .padding(horizontal = 15.dp)
                    )

                }
            }

        }
    }
}

@Composable
private fun MainContent(
    mMonth: String,
    mYear : String,
    mDay : String,
    minYear: Int,
    maxYear: Int,
    shortWeekDays : Boolean,
    selectedPart : String,
    setDay : (String) -> Unit,
    setMonth: (String) -> Unit,
    setYear: (String) -> Unit,
    setSelected : (String) -> Unit
){

    val width = LocalConfiguration.current.screenWidthDp
    val persianWeekDays = listOf("شنبه","یکشنبه","دوشنبه","سه شنبه",
        "چهارشنبه","پنجشنبه","جمعه", )

    val monthsList = listOf("فروردین", "اردیبهشت", "خرداد", "تیر",
        "مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند",)
    val weekDay = JalaliCalendarD(mYear.toInt(),
        monthsList.indexOf(mMonth) + 1, mDay.toInt()).dayOfWeek

    var inputFormat by remember {
        // manual, selection
        mutableStateOf("selection")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {

            Row(
                Modifier
                    .height(30.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 10.dp, horizontal = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                //Text(text = "انتخاب تاریخ", color = MaterialTheme.colors.onPrimary, style = MaterialTheme.typography.body2)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 10.dp, horizontal = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            inputFormat = if (inputFormat == "selection") {
                                "manual"
                            } else {
                                "selection"
                            }
                        }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ){
                    Text(
                        text = mMonth,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .clickable {
                                setSelected("month")
                            }
                    )
                    Text(text = mDay, style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Text(text = persianWeekDays[weekDay - 1] , style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 5.dp))

                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row() {
                    Text(
                        text = mYear,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(.7f),
                        modifier = Modifier.clickable {
                            setSelected("year")
                        }
                    )
                    Text(
                        text = mMonth,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(.7f),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                setSelected("month")
                            }
                    )
                }
                
                Row() {
                    if (selectedPart == "main") {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(.8f),
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clip(CircleShape)
                                .clickable {
                                    decreaseMonth(
                                        mMonth,
                                        mYear,
                                        setMonth = { setMonth(it) },
                                        setYear = { setYear(it) })
                                }
                        )

                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(.8f),
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clip(CircleShape)
                                .clickable {
                                    increaseMonth(
                                        mMonth,
                                        mYear,
                                        setMonth = { setMonth(it) },
                                        setYear = { setYear(it) })
                                }
                        )
                    }
                }
                
            }

            Crossfade(targetState = inputFormat, label = "") { format ->
                when(format){
                    "selection" ->
                        Crossfade(selectedPart, label = "") { part ->
                            when(part){
                                "main" -> Days(mMonth, mDay, mYear, shortWeekDays,setDay = {setDay(it)}, changeSelectedPart = {})
                                "month" -> Months(mMonth,{setSelected("main")} ){setMonth(it)}
                                "year" -> Years(mYear, minYear, maxYear,setYear = {setYear(it)}, changeSelectedPart = {setSelected("main")})
                                else -> Days(mMonth, mDay, mYear,shortWeekDays, setDay = {setDay(it)}, changeSelectedPart = {})
                            }
                        }
                    "manual" -> ManualDatePick( { setDay(it) }, {setMonth(it)}, {setYear(it)})
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualDatePick(
    setDay: (String) -> Unit,
    setMonth: (String) -> Unit,
    setYear: (String) -> Unit
){

    val monthsList = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد"
        ,"شهریور","مهر","آبان","آذر","دی","بهمن","اسفند",)

    var cDay by remember {
        mutableStateOf("")
    }

    var cMonth by remember {
        mutableStateOf("")
    }

    var cYear by remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 35.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedTextField(
            value = cYear,
            onValueChange = {
                cYear = it
                setManualYear(it){v->
                    setYear(it)
                }
            },
            textStyle = MaterialTheme.typography.bodySmall,
            label = { Text(text = "سال")},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus()}
            ),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.width(70.dp),
            shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
            value = cMonth,
            onValueChange = {

                setManualMonth(it, {cMonth = it}){ v ->
                    setMonth(monthsList[v.toInt() - 1])
                }
                            },
            textStyle = MaterialTheme.typography.bodySmall,
            label = { Text(text = "ماه")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Previous),
            keyboardActions = KeyboardActions(
                onPrevious = { focusManager.moveFocus(FocusDirection.Left)}
            ),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.width(70.dp),
            shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
            value = cDay,
            onValueChange = {
                cDay = it
                setManualDay(it){ v ->
                    setDay(v)
                } },
            textStyle = MaterialTheme.typography.labelLarge,
            label = { Text(text = "روز")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Previous),
            keyboardActions = KeyboardActions(
                onPrevious = { focusManager.moveFocus(FocusDirection.Left)},
            ),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.width(60.dp),
            shape = RoundedCornerShape(10.dp)
        )

    }
}

private fun setManualDay(value: String, setDay: (String) -> Unit){
    if (value.isNotEmpty() && value.isDigitsOnly()){
        when{
            value.toInt() < 1 -> setDay("1")
            value.toInt() > 31 -> setDay("31")
            else -> setDay(value)
        }
    }
}

private fun setManualMonth(value: String, setCMonth: (String) -> Unit,setMonth: (String) -> Unit){
    if (value != "" && value.isDigitsOnly()){
        if (value.toInt() in 1..12){
            setCMonth(value)
        }
    } else {
        setMonth(value)
    }

    if (value.isNotEmpty() && value.isDigitsOnly()){
        when{
            value.toInt() < 1 -> setMonth("1")
            value.toInt() > 12 -> setMonth("12")
            else -> setMonth(value)
        }
    }
}

private fun setManualYear(value: String, setYear: (String) -> Unit){
    if (value.isNotEmpty() && value.isDigitsOnly()){
        if (value.length == 4){
            setYear(value)
        }
    }
}

private fun increaseMonth(mMonth: String, mYear: String,setMonth: (String) -> Unit, setYear: (String) -> Unit) {
    val monthsList = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند",)
    if (monthsList.indexOf(mMonth) < 10){
        setMonth(monthsList[monthsList.indexOf(mMonth) + 1])
    } else {
        setMonth(monthsList[0])
        setYear((mYear.toInt() + 1).toString())
    }
}

private fun decreaseMonth(mMonth: String, mYear: String,setMonth: (String) -> Unit, setYear: (String) -> Unit) {
    val monthsList = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند",)
    if (monthsList.indexOf(mMonth) > 0){
        setMonth(monthsList[monthsList.indexOf(mMonth) - 1])
    } else {
        setMonth(monthsList[11])
        setYear((mYear.toInt() - 1).toString())
    }
}


@Composable
private fun Months(mMonth : String, setSelected: () -> Unit ,setMonth : (String) -> Unit){

    val monthsList = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند",)

    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ){
        items(monthsList){
            Surface(
                modifier = Modifier
                    .padding(5.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable {
                        setMonth(it)
                        setSelected()
                    }
                ,
                shape = CircleShape,
                color = if (mMonth == it) MaterialTheme.colorScheme.primary else Color.Transparent,

                ) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = it,
                        color = if (mMonth == it) MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}


@Composable
private fun Days(mMonth: String,mDay : String , mYear: String ,shortWeekDays: Boolean, setDay :
    (String) -> Unit, changeSelectedPart : (String) -> Unit){

    val monthsList = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند",)
    val weekDays = listOf("شنبه","یکشنبه","دوشنبه","سه شنبه","چهارشنبه","پنجشنبه","جمعه", )
    val persianWeekDaysShort = listOf("ش","ی","د","س","چ","پ","ج",)

    var weekDay = JalaliCalendarD(mYear.toInt(), monthsList.indexOf(mMonth) + 1 , 1).dayOfWeek

    var today = JalaliCalendarD().day
    val thisMonth = JalaliCalendarD().month -1
    Log.i("TAG_month","$thisMonth")

    var daysList = mutableListOf<String>()

    Log.i("TAG_weekday", "$weekDay")

    if (weekDay != 7){
        for (i in 1..weekDay){
            daysList.add(" ")
        }
    }

    if (monthsList.indexOf(mMonth) < 6){
        for (i in 1..31){
            daysList.add(i.toString())
        }
    } else {
        for (i in 1..30){
            daysList.add(i.toString())
            if (mDay.toInt() > 30){
                setDay("30")
            }
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp, horizontal = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (shortWeekDays){
                persianWeekDaysShort.forEach{
                    Text(text = it, color = MaterialTheme.colorScheme.primary.copy(.4f),
                        style = MaterialTheme.typography.bodySmall)
                }
            } else {
                weekDays.forEach{
                    Text(text = it, color = MaterialTheme.colorScheme.primary.copy(.4f),
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal =  15.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.Center
        ){
            items(daysList){
                Surface(
                    modifier = Modifier
                        .padding(vertical = 1.dp)
                        .size(45.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (it != " ") {
                                changeSelectedPart("main")
                                setDay(it)
                            }
                        },
                    shape = CircleShape,
                    color = if (mDay == it) MaterialTheme.colorScheme.primary else Color.Transparent,
                    border = BorderStroke( 1.dp, color = if (it == today.toString() &&
                        monthsList.indexOf(mMonth) == thisMonth)
                        MaterialTheme.colorScheme.primary else Color.Transparent)
                ) {
                    Row(Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Text(text = it, style = MaterialTheme.typography.bodyLarge,
                            color = if (mDay == it) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground.copy(.7f))
                    }
                }
            }
        }

    }
}


@Composable
private fun Years(mYear: String, minYear: Int, maxYear: Int, setYear :
    (String) -> Unit, changeSelectedPart: (String) -> Unit){

    var years = mutableListOf<Int>()
    for (y in maxYear downTo minYear){
        years.add(y)
    }

    var gridState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = 1){
        scope.launch {
            gridState.scrollToItem(28)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = gridState
    ){
        items(years){
            Surface(
                modifier = Modifier
                    .padding(15.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable {
                        changeSelectedPart("main")
                        setYear(it.toString())
                    },
                shape = CircleShape,
                color = if (mYear == it.toString()) MaterialTheme.colorScheme.primary else Color.Transparent
            ) {
                Row(Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Text(text = it.toString(), style = MaterialTheme.typography.bodyMedium,
                        color = if (mYear == it.toString()) Color.White else MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

}



