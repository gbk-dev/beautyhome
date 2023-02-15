package com.example.beautyhome.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
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
import com.example.domain.models.Record
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
fun MainScreen(
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
        TopBar(selections = selections, navController = navController, viewModel = viewModel)
        Spacer(modifier = Modifier.height(56.dp))
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    isSelected = selections.contains(day),
                    onClick = { clickDay ->
                        if (selections.contains(clickDay)){
                            selections.remove(clickDay)
                        } else {
                            selections.add(clickDay)
                        }
                    },
                    viewModel = viewModel)
            },
            monthHeader = { month ->
                MonthHeader(month = month, year = Year.now(), onClick = {
                    scope.launch {
                        state.animateScrollToMonth(it)
                    }
                })
                val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                DaysOfWeekTitle(daysOfWeek = daysOfWeek)
            },
            modifier = Modifier.background(color = LightPink)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TopBar(
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
                    popUpTo(Screens.Main.route) { inclusive = false }
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = "profile", tint = Purple200)
        }
        
        TextButton(
            onClick = {
                navController.navigate(Screens.ListOfRecords.route){
                    popUpTo(Screens.Main.route) { inclusive = false }
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

        var pickedTime by remember {
            mutableStateOf(LocalTime.NOON)
        }

        val timeDialogState = rememberMaterialDialogState()
        val serviceDialogState = rememberMaterialDialogState()

        if (selections.isNotEmpty()){
            IconButton(onClick = {
                selections.clear()
            }) {
                Icon(imageVector = Icons.Outlined.Clear, contentDescription = null, tint = Purple200)
            }
            IconButton(onClick = {
                serviceDialogState.show()
            }) {
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

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    val user = viewModel.userList.value
                    val today = LocalDate.now()
                    for (i in selections){
                        val record = Record(
                            date = i.date.toString(),
                            time = pickedTime.toString(),
                            service = service.value,
                            userName = "${user?.firstName} ${user?.lastName}",
                            phone = "123"
                        )
                        selections.clear()
                        if (i.date > today){
                            viewModel.clientRecord(record = record)
                        }
                    }
                }
                negativeButton(text = "Cancel")
            }
        ) {
            val startTime = "08:00"
            val endTime = "22:00"
            val startTimeFormat = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))
            val endTimeFormat = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))
            val range = startTimeFormat..endTimeFormat
            timepicker(
                title = "Pick a time",
                is24HourClock = true,
                timeRange = range
            ) {
                pickedTime = it
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit, viewModel: RecordViewModel) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square sizing!
            .clip(CircleShape)
            .background(
                color = if (isSelected) {
                    Purple200
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = { onClick(day) }),
        contentAlignment = Alignment.Center
    ) {
        val textDay = day.date.dayOfMonth.toString()
        val textColor = remember { mutableStateOf(Color.Black) }
        val listActiveDay = viewModel.allRecordList.value.orEmpty()
        listActiveDay.forEach {
            val date = LocalDate.parse(it.date)
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
            fontSize = 14.sp,
        )
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
                color = Color.Black
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