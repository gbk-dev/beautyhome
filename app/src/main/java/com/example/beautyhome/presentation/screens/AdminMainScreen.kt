package com.example.beautyhome.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.core.util.toRange
import androidx.navigation.NavController
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.RecordViewModel
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.LightPink
import com.example.beautyhome.ui.theme.Purple200
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beautyhome.presentation.navigation.AuthScreens
import com.example.beautyhome.ui.theme.Purple700
import com.example.domain.models.Record
import com.example.domain.models.TimeSchedule
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AdminMainScreen(
    viewModel: RecordViewModel = viewModel(),
    navController: NavController
){
    viewModel.getAllRecord()
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

    Column(
        modifier = Modifier
            .background(color = DefBlack)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        AdminTopBar(selections = selections, navController = navController, viewModel = viewModel)
        Spacer(modifier = Modifier.height(56.dp))
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                AdminDay(
                    day = day,
                    isSelected = selections.contains(day),
                    onClick = { clickDay ->
                        if (selections.contains(clickDay)){
                            selections.remove(clickDay)
                        } else {
                            selections.add(clickDay)
                        }
                    },
                    viewModel = viewModel
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
            modifier = Modifier.background(color = LightPink)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminTopBar(
    selections: SnapshotStateList<CalendarDay>,
    navController: NavController,
    viewModel: RecordViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        IconButton(
            onClick = {
                navController.navigate(Screens.Profile.route){
                    popUpTo(Screens.AdminMain.route) { inclusive = false }
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = "profile", tint = Purple200)
        }

        TextButton(
            onClick = {
                navController.navigate(Screens.ListOfRecords.route){
                    popUpTo(Screens.AdminMain.route) { inclusive = false }
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.List, contentDescription = null)
            Text(text = "Список")
        }

        Spacer(Modifier.weight(1f, true))

        IconButton(onClick = {
            viewModel.signOut()
            navController.navigate(AuthScreens.SignIn.route)
        }) {
            Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = null)
        }

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
fun AdminDay(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit, viewModel: RecordViewModel) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square sizing!
            .clip(CircleShape)
            .background(
                color = if (isSelected) {
                    Purple700
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = { onClick(day) }),
        contentAlignment = Alignment.Center
    ) {
        viewModel.getTimeSchedule()
        var textDay = day.date.dayOfMonth.toString()
        var fontSize = 14.sp
        val textColor = remember { mutableStateOf(Color.Black) }
        val listActiveDay = viewModel.allRecordList.value.orEmpty()
        val timeScheduleList = viewModel.timeScheduleList.value.orEmpty()
        if (timeScheduleList.isNotEmpty()){
            timeScheduleList.forEach { timeSchedule ->
                val date = LocalDate.parse(timeSchedule.date)
                val timeFirst = timeSchedule.time.first()
                val timeLast = timeSchedule.time.last()
                if (date == day.date){
                    textDay = "$textDay\n$timeFirst-$timeLast"
                    fontSize = 8.sp
                }
            }
        }
        listActiveDay.forEach { record ->
            val date = LocalDate.parse(record.date)
            if (day.date == date){
                textColor.value = Color.Cyan
            } else {
                when (day.position) {
                    DayPosition.MonthDate -> if (isSelected) textColor.value = Color.White else textColor.value = Color.Black
                    DayPosition.InDate, DayPosition.OutDate -> textColor.value = Color.Gray
                }
            }
        }
        Text(
            text = textDay,
            color = textColor.value,
            fontSize = fontSize,
        )
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
                color = Color.Black
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
            Icon(imageVector = leftArrow, contentDescription = null)
        }
        Text(
            text = "$monthLocale, ${year.atMonth(month.yearMonth.month)}",
            color = Color.Black
        )
        IconButton(onClick = {
            onClick(month.yearMonth.plusMonths(1))
        }) {
            Icon(imageVector = rightArrow, contentDescription = null)
        }
    }
}