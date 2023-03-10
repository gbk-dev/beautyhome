package com.example.beautyhome.presentation.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.time.timepicker
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

    viewModel.getUser()
    viewModel.getRecord()
    viewModel.getAllRecord()
    viewModel.getTimeScheduleList()

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

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            backgroundColor = CalendarBack,
            border = BorderStroke(0.5.dp, Purple200),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.background(color = Color.Transparent)
            ) {
                viewModel.getRecord().runCatching {
                    val record = viewModel.record.value
                    if (record != null){
                        val service = record.service
                        val date = record.date
                        val time = record.time

                        val fontSize = 18.sp
                        val color = Purple

                        Spacer(modifier = Modifier.height(8.dp))
                        service.forEach {
                            if (it != ""){
                                Text(
                                    text = "???????????? - $it",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, 8.dp),
                                    color = color,
                                    fontSize = fontSize
                                )
                            }
                        }

                        Text(
                            text = "???????? - $date",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, 8.dp),
                            color = color,
                            fontSize = fontSize
                        )
                        Text(
                            text = "?????????? - $time",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, 8.dp, bottom = 16.dp),
                            color = color,
                            fontSize = fontSize
                        )
                    }
                }
            }
        }
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
            Text(text = "??????????????", color = Purple200)
        }

        Spacer(Modifier.weight(1f, true))

        val pickedTimeList = remember { mutableStateOf(mutableListOf(LocalTime.NOON)) }

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

        val service = remember { mutableStateOf(mutableListOf("")) }
        val listService : List<String> = service.value
        val listTimeMut: MutableList<String> = mutableListOf()
        val listTime: List<String> = listTimeMut

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
                    service.value.clear()
                    pickedTimeList.value.clear()
                }
            }
        ) {
            val serviceList = listOf("??????????", "??????????????", "??????????")
            title(text = "???????????????? ????????????")
            listItemsMultiChoice(
                list = serviceList,
            ) { listPressed ->
                service.value.clear()
                listPressed.forEach {
                    service.value.add(serviceList[it])
                }

            }

            service.value.forEach {
                if (it == ""){
                    service.value.remove(it)
                }
            }
        }
        val context = LocalContext.current

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    setRecord(
                        viewModel = viewModel,
                        selectedDate = selectedDate,
                        pickedTimeList = pickedTimeList,
                        context = context,
                        listService = listService,
                        listTime = listTime,
                        listTimeMut = listTimeMut
                    )
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
            if (startTime.value == "" && endTime.value == "") {
                Toast.makeText(context, "???? ?????????????? ????????", Toast.LENGTH_SHORT).show()
            } else {
                val startTimeFormat = LocalTime.parse(startTime.value, DateTimeFormatter.ofPattern("HH:mm"))
                val endTimeFormat = LocalTime.parse(endTime.value, DateTimeFormatter.ofPattern("HH:mm"))
                val range = startTimeFormat..endTimeFormat
                timepicker(
                    title = "???????????????? ??????????",
                    is24HourClock = true,
                    timeRange = range
                ) {
                    pickedTimeList.value.clear()
                    listTimeMut.clear()
                    pickedTimeList.value.add(it)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: (CalendarDay) -> Unit,
    timeScheduleList: List<TimeSchedule>,
    record: Record?
) {
    var textDayTime by remember { mutableStateOf("") }
    var isWorkDay = false
    var textColor by remember { mutableStateOf(darkWhite) }
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
            if (record != null) {
                val date = LocalDate.parse(record.date)
                textColor = if (today <= date && date == day.date && !isSelected) {
                    Purple
                } else if (today <= date && date == day.date && isSelected) {
                    DefBlack
                } else {
                    darkWhite
                }
            } else {
                textColor = if (isSelected) {
                    DefBlack
                } else {
                    darkWhite
                }
            }
        }
        DayPosition.InDate, DayPosition.OutDate -> {
            if (record != null){
                val date = LocalDate.parse(record.date)
                textColor = if (today <= date && date == day.date && !isSelected) {
                    Purple
                } else if (today <= date && date == day.date && isSelected) {
                    DefBlack
                } else {
                    Color.Gray
                }
            } else {
                textColor = if (isSelected) {
                    DefBlack
                } else {
                    Color.Gray
                }
            }
        }
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
                    color = textColor,
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
                    color = textColor,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 2.dp, end = 2.dp)
                )
            }

        } else {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
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

@RequiresApi(Build.VERSION_CODES.O)
fun setRecord(viewModel: RecordViewModel, selectedDate: LocalDate?, pickedTimeList : MutableState<MutableList<LocalTime>>, context: Context, listService: List<String>, listTime: List<String>, listTimeMut: MutableList<String>){
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
                        pickedTimeList.value.forEach { pickedTime ->
                            if (timeScheduleTime == pickedTime.toString()) {
                                if (activeRecordList.isNotEmpty()){
                                    activeRecordList.forEach { activeRecords ->
                                        val activeDate = LocalDate.parse(activeRecords.date)
                                        if (activeDate == date && activeRecords.time == listOf(pickedTime.toString())){
                                            Toast.makeText(context, "?????? ?????????? ?????? ????????????", Toast.LENGTH_SHORT).show()
                                        } else {

                                            listTimeMut.clear()

                                            for (i in listService.indices) {
                                                if (i < listService.size){
                                                    listTimeMut.add(pickedTime.plusHours(i.toLong()).toString())
                                                }
                                            }

                                            val date1 = LocalTime.parse(listTime.last())
                                            val date2 = LocalTime.parse(timeSchedule.time.last())

                                            if (date1 <= date2) {
                                                val record = Record(
                                                    date = selectedDate.toString(),
                                                    time = listTime,
                                                    service = listService,
                                                    userName = "${user?.firstName} ${user?.lastName}",
                                                    phone = user?.phone!!
                                                )
                                                if (selectedDate > today){
                                                    viewModel.clientRecord(record = record).runCatching {
                                                        Toast.makeText(context, "???? ???????????????????? ???? ???????? ${record.date} ?? ${record.time}", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Toast.makeText(context, "???? ???????? ???????? ?????? ???????????? ????????????????????", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(context, "12331313313", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                } else {
                                    val record = Record(
                                        date = selectedDate.toString(),
                                        time = listTimeMut,
                                        service = listService,
                                        userName = "${user?.firstName} ${user?.lastName}",
                                        phone = user?.phone!!
                                    )
                                    if (selectedDate > today){
                                        viewModel.clientRecord(record = record).runCatching {
                                            Toast.makeText(context, "???? ???????????????????? ???? ???????? ${record.date} ?? ${record.time}", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "???? ???????? ???????? ?????? ???????????? ????????????????????", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Toast.makeText(context, "?????????????????? ????????", Toast.LENGTH_SHORT).show()
    }
}