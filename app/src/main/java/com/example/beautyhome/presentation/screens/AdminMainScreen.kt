package com.example.beautyhome.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.RecordViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.util.*
import com.example.beautyhome.ui.theme.*
import com.example.domain.models.Record
import com.example.domain.models.TimeSchedule
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import java.time.*

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AdminMainScreen(
    viewModel: RecordViewModel,
    navController: NavController
){
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val selections = remember { mutableStateListOf<CalendarDay>() }
    val scope = rememberCoroutineScope()
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = CalendarBack)
                ){
                    viewModel.getAllRecord().runCatching {
                        val recordsList = viewModel.allRecordList.value.orEmpty()
                        if (selections.isNotEmpty()){
                            selections.forEach { selected ->
                                if (recordsList.isNotEmpty()){
                                    recordsList.forEach { record ->
                                        val selectDate = selected.date
                                        val recordDate = LocalDate.parse(record.date)
                                        if (selectDate == recordDate){
                                            items(listOf(record)){ records ->
                                                val data = records.date
                                                val userName = records.userName
                                                val time = records.time
                                                val service = records.service
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp)
                                                        .background(color = Color.Transparent),
                                                    border = BorderStroke(1.dp, Purple200),
                                                    shape = RoundedCornerShape(20.dp),
                                                    backgroundColor = CalendarBack
                                                ) {
                                                    Column(modifier = Modifier.fillMaxSize()) {
                                                        Text(text = "Пользователь - $userName", modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp, end = 16.dp), color = darkWhite)
                                                        Text(text = "Услуга - $service", modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp), color = darkWhite)
                                                        Text(text = "Дата - $data", modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp), color = darkWhite)
                                                        Text(text = "Время - $time", modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp), color = darkWhite)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
                       },
        sheetPeekHeight = 0.dp,
        sheetBackgroundColor = CalendarBack,
        sheetShape = Shapes.small
    ) {
        Column(
            modifier = Modifier
                .background(color = DefBlack)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            AdminTopBar(
                selections = selections,
                navController = navController,
                viewModel = viewModel,
                scope = scope,
                bottomSheetScaffoldState = bottomSheetScaffoldState
            )
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    viewModel.getAllRecord()
                    val recordsList = viewModel.allRecordList.value.orEmpty()
                    viewModel.getTimeScheduleList()
                    val timeScheduleList = viewModel.timeScheduleList.value.orEmpty()
                    AdminDay(
                        day = day,
                        isSelected = selections.contains(day),
                        onClick = { clickDay ->
                            if (selections.contains(clickDay)){
                                selections.remove(clickDay)
                            } else {
                                selections.add(clickDay)
                            }
                            if (recordsList.isNotEmpty()){
                                recordsList.forEach {
                                    val date = LocalDate.parse(it.date)
                                    if (date == clickDay.date){
                                        scope.launch {
                                            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed){
                                                bottomSheetScaffoldState.bottomSheetState.expand()
                                            } else {
                                                bottomSheetScaffoldState.bottomSheetState.collapse()
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        recordsList = recordsList,
                        timeScheduleList = timeScheduleList
                    )
                },
                monthHeader = { month ->
                    AdminMonthHeader(month = month, year = Year.now(), onClick = {
                        scope.launch {
                            state.animateScrollToMonth(it)
                        }
                    })
                    val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                    AdminDaysOfWeekTitle(daysOfWeek = daysOfWeek)
                },
                modifier = Modifier.background(color = CalendarBack)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminTopBar(
    selections: SnapshotStateList<CalendarDay>,
    navController: NavController,
    viewModel: RecordViewModel,
    scope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {
                navController.navigate(Screens.Profile.route){
                    popUpTo(Screens.AdminMain.route) { inclusive = false }
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = "profile", tint = Purple200)
            Text(text = "Профиль", color = Purple200)
        }

        TextButton(
            onClick = {
                navController.navigate(Screens.ListOfRecords.route){
                    popUpTo(Screens.AdminMain.route) { inclusive = false }
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.List, contentDescription = null, tint = Purple200)
            Text(text = "Список", color = Purple200)
        }

        Spacer(Modifier.weight(1f, true))

        var pickedFirstTime by remember {
            mutableStateOf(LocalTime.NOON)
        }
        var pickedSecondTime by remember {
            mutableStateOf(LocalTime.NOON)
        }

        val firstTimeDialogState = rememberMaterialDialogState()
        val secondTimeDialogState = rememberMaterialDialogState()

        if (selections.isNotEmpty()){
            IconButton(onClick = {
                selections.clear()
                scope.launch {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }) {
                Icon(imageVector = Icons.Outlined.Clear, contentDescription = null, tint = Purple200)
            }
            IconButton(onClick = {
                firstTimeDialogState.show()
            }) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null, tint = Purple200)
            }
        }

        viewModel.getUser()

        MaterialDialog(
            dialogState = firstTimeDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    secondTimeDialogState.show()
                }
                negativeButton(text = "Cancel")
            }
        ) {
            timepicker(
                title = "Рабочее время с",
                is24HourClock = true
            ) {
                pickedFirstTime = it
            }
        }

        MaterialDialog(
            dialogState = secondTimeDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    val timeRange = pickedFirstTime..pickedSecondTime
                    selections.forEach {
                        var time = timeRange.start
                        val listTime = mutableListOf<String>()
                        while (time <= timeRange.endInclusive){
                            listTime.add(time.toString())
                            time = time.plusHours(1)
                        }
                        val timeSchedule = TimeSchedule(date = it.date.toString(), time = listTime)
                        viewModel.setTimeSchedule(timeSchedule)
                    }
                }
                negativeButton(text = "Cancel")
            }
        ) {
            timepicker(
                title = "Рабочее время по",
                is24HourClock = true
            ) {
                pickedSecondTime = it
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminDay(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit, recordsList: List<Record>, timeScheduleList: List<TimeSchedule>) {

    var textDayTime = ""
    var isWorkDay = false
    val textColor = remember { mutableStateOf(darkWhite) }

    if (timeScheduleList.isNotEmpty()){
        timeScheduleList.forEach {
            val dateTimeSchedule = LocalDate.parse(it.date)
            val timeFirst = it.time.first()
            val timeLast = it.time.last()
            if (dateTimeSchedule == day.date){
                isWorkDay = true
                textDayTime = "$timeFirst\n$timeLast"
            }
        }
    }

    if (recordsList.isNotEmpty()){
        recordsList.forEach {
            val dateRecord = LocalDate.parse(it.date)
            when (day.position) {
                DayPosition.MonthDate -> {
                    if (dateRecord == day.date && !isSelected){
                        textColor.value = Purple
                    }
                    if (dateRecord == day.date && isSelected){
                        textColor.value = DefBlack
                    }
                }
                DayPosition.InDate, DayPosition.OutDate -> {
                    if (dateRecord == day.date){
                        textColor.value = Purple
                    }
                    if (dateRecord != day.date) {
                        textColor.value = Color.Gray
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (isSelected) {
                    Purple
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = { onClick(day) })
    ) {

        if (isWorkDay){
            Box(
                modifier = Modifier
                    .aspectRatio(1f),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = textColor.value,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .aspectRatio(1f),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = textDayTime,
                    color = textColor.value,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 2.dp, end = 2.dp)
                )
            }

        } else {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor.value,
                fontSize = 14.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminDaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                color = darkWhite
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminMonthHeader(month: CalendarMonth, year: Year, onClick: (YearMonth) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val monthLocale = month.yearMonth.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
        val leftArrow = Icons.Outlined.KeyboardArrowLeft
        val rightArrow = Icons.Outlined.KeyboardArrowRight
        IconButton(onClick = {
            onClick(month.yearMonth.minusMonths(1))
        }) {
            Icon(imageVector = leftArrow, contentDescription = null, tint = Purple200)
        }
        Text(
            text = "$monthLocale, ${year.atMonth(month.yearMonth.month)}",
            color = darkWhite
        )
        IconButton(onClick = {
            onClick(month.yearMonth.plusMonths(1))
        }) {
            Icon(imageVector = rightArrow, contentDescription = null, tint = Purple200)
        }
    }
}