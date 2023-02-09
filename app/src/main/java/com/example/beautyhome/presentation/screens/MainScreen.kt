package com.example.beautyhome.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.Purple200
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Year
import java.time.YearMonth
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(){

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    val selections = remember { mutableStateListOf<CalendarDay>() }
    val listDate= remember { mutableListOf<CalendarDay>() }
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
        TopBar(selections = selections, listDate = listDate)
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
                            listDate.remove(clickDay)
                        } else {
                            selections.add(clickDay)
                            listDate.add(clickDay)
                        }

                        Log.e("MainScreen", listDate.toString())
                    })
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
            modifier = Modifier.background(color = Color.White)
        )
    }
}

@Composable
fun TopBar(selections: SnapshotStateList<CalendarDay>, listDate: MutableList<CalendarDay>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = "profile", tint = Purple200)
        }
        Spacer(Modifier.weight(1f, true))

        if (selections.isNotEmpty()){
            IconButton(onClick = {
                selections.clear()
                listDate.clear()
            }) {
                Icon(imageVector = Icons.Outlined.Clear, contentDescription = null, tint = Purple200)
            }
            IconButton(onClick = {

            }) { Icon(imageVector = Icons.Outlined.Add, contentDescription = null, tint = Purple200) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
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
        val textColor = when (day.position) {
            DayPosition.MonthDate -> if (isSelected) Color.White else Color.Black
            DayPosition.InDate, DayPosition.OutDate -> Color.Gray
        }
        val textDay = day.date.dayOfMonth.toString()
        val dot = "."
        Text(
            text = textDay,
            color = textColor,
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