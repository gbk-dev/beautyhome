package com.example.beautyhome.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserMainScreen(
    viewModel: RecordViewModel,
    navController: NavController
){
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
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
        TopBar(
            selectedDate = selectedDate,
            navController = navController,
            viewModel = viewModel
        )
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                viewModel.getTimeScheduleList()
                viewModel.getRecord()
                val timeScheduleList = viewModel.timeScheduleList.value.orEmpty()
                val record = viewModel.record.value
                Day(
                    day = day,
                    isSelected = selectedDate == day.date,
                    onClick = { clickDay ->
                        selectedDate = if (selectedDate == clickDay.date) null else day.date
                    },
                    timeScheduleList = timeScheduleList,
                    record = record
                )
            },
            monthHeader = { month ->
                MonthHeader(
                    month = month,
                    year = Year.now(),
                    onClick = {
                        scope.launch {
                            state.animateScrollToMonth(it)
                        }
                    }
                )
                val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                DaysOfWeekTitle(daysOfWeek = daysOfWeek)
            },
            modifier = Modifier.background(color = CalendarBack)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TopBar(
    selectedDate: LocalDate?,
    navController: NavController,
    viewModel: RecordViewModel
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
                    popUpTo(Screens.UserMain.route) { inclusive = false }
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = "profile", tint = Purple200)
            Text(text = "Профиль", color = Purple200)
        }
        
        Spacer(Modifier.weight(1f, true))

        var pickedTime by remember {
            mutableStateOf(LocalTime.NOON)
        }

        val timeDialogState = rememberMaterialDialogState()
        val serviceDialogState = rememberMaterialDialogState()

        if (selectedDate != null){
            IconButton(
                onClick = {
                    serviceDialogState.show()
                }
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null, tint = Purple200)
            }
        }
        viewModel.getUser()
        val service = remember { mutableStateOf("") }
        MaterialDialog(
            dialogState = serviceDialogState,
            buttons = {
                positiveButton(text = "Ok"){
                    timeDialogState.show()
                }
                negativeButton(text = "cancel"){
                    if (timeDialogState.showing){
                        timeDialogState.hide()
                    }
                }
            }
        ) {
            title(text = "Выберите услугу")
            listItemsSingleChoice(
                list = listOf("Брови", "Ресницы", "Ногти"),
            ) {
                when (it) {
                    0 -> service.value = "Брови"
                    1 -> service.value = "Ресницы"
                    2 -> service.value = "Ногти"
                }
            }
        }
        val context = LocalContext.current

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    val user = viewModel.user.value
                    val today = LocalDate.now()
                    val timeScheduleList = viewModel.timeScheduleList.value.orEmpty()
                    val activeRecordList = viewModel.allRecordList.value.orEmpty()
                    if (timeScheduleList.isNotEmpty()){
                        timeScheduleList.forEach { timeSchedule ->
                            val date = LocalDate.parse(timeSchedule.date)
                            if (selectedDate != null){
                                if (date == selectedDate){
                                    timeSchedule.time.forEach { timeScheduleTime ->
                                        if (timeScheduleTime == pickedTime.toString()){
                                            if (activeRecordList.isNotEmpty()){
                                                activeRecordList.forEach { activeRecords ->
                                                    val activeDate = LocalDate.parse(activeRecords.date)
                                                    if (activeDate == date && activeRecords.time == pickedTime.toString()){
                                                        Toast.makeText(context, "Это время уже занято", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        val record = Record(
                                                            date = selectedDate.toString(),
                                                            time = pickedTime.toString(),
                                                            service = service.value,
                                                            userName = "${user?.firstName} ${user?.lastName}",
                                                            phone = user?.phone!!
                                                        )
                                                        if (selectedDate > today){
                                                            viewModel.clientRecord(record = record)
                                                            Toast.makeText(context, "Вы записались на дату ${record.date} в ${record.time}", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            Toast.makeText(context, "На этот день уже нельзя записаться", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            } else {
                                                val record = Record(
                                                    date = selectedDate.toString(),
                                                    time = pickedTime.toString(),
                                                    service = service.value,
                                                    userName = "${user?.firstName} ${user?.lastName}",
                                                    phone = user?.phone!!
                                                )
                                                if (selectedDate > today){
                                                    viewModel.clientRecord(record = record)
                                                    Toast.makeText(context, "Вы записались на дату ${record.date} в ${record.time}", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "На этот день уже нельзя записаться", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                negativeButton(text = "Cancel")
            }
        ) {
            viewModel.getTimeScheduleList()
            val startTime = remember { mutableStateOf("") }
            val endTime = remember { mutableStateOf("") }
            val timeScheduleList = viewModel.timeScheduleList.value.orEmpty()
            if (timeScheduleList.isNotEmpty()){
                timeScheduleList.forEach { timeSchedule ->
                    val date = LocalDate.parse(timeSchedule.date)
                    if (selectedDate == date){
                        startTime.value = timeSchedule.time.first()
                        endTime.value = timeSchedule.time.last()
                    }
                }
            }
            if (startTime.value == "" && endTime.value == ""){
                Toast.makeText(context, "Не рабочий день", Toast.LENGTH_SHORT).show()
            } else {
                val startTimeFormat = LocalTime.parse(startTime.value, DateTimeFormatter.ofPattern("HH:mm"))
                val endTimeFormat = LocalTime.parse(endTime.value, DateTimeFormatter.ofPattern("HH:mm"))
                val range = startTimeFormat..endTimeFormat
                timepicker(
                    title = "Выберете время",
                    is24HourClock = true,
                    timeRange = range
                ) {
                    pickedTime = it
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit, timeScheduleList: List<TimeSchedule>, record: Record?) {
    var textDayTime = ""
    var isWorkDay = false
    val textColor = remember { mutableStateOf(darkWhite) }
    val today = LocalDate.now()
    if (timeScheduleList.isNotEmpty()){
        timeScheduleList.forEach { timeSchedule ->
            val date = LocalDate.parse(timeSchedule.date)
            val timeFirst = timeSchedule.time.first()
            val timeLast = timeSchedule.time.last()
            if (date == day.date && today <= date){
                isWorkDay = true
                textDayTime = "$timeFirst\n$timeLast"
            }
        }
    }

    when (day.position) {
        DayPosition.MonthDate -> {
            if (record != null){
                val date = LocalDate.parse(record.date)
                if (today <= date && date == day.date && !isSelected){
                    textColor.value = Purple
                } else if (today <= date && date == day.date && isSelected) {
                    textColor.value = DefBlack
                } else {
                    textColor.value = darkWhite
                }
            } else {
                if (isSelected) {
                    textColor.value = DefBlack
                } else {
                    textColor.value = darkWhite
                }
            }
        }
        DayPosition.InDate, DayPosition.OutDate -> textColor.value = Color.Gray
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(color = if (isSelected) Purple else Color.Transparent)
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
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
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
fun MonthHeader(month: CalendarMonth, year: Year, onClick: (YearMonth) -> Unit) {
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