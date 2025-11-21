package com.example.calendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calendar.models.Event
import com.example.calendar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarPage(
    events: List<Event>,
    selectedDate: Date,
    onDateSelect: (Date) -> Unit,
    onAddEvent: () -> Unit,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDate by remember { mutableStateOf(Date()) }
    val calendar = Calendar.getInstance()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(bottom = 160.dp),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Calendar Card
            item {
                CalendarCard(
                    currentDate = currentDate,
                    selectedDate = selectedDate,
                    events = events,
                    onDateSelect = onDateSelect,
                    onMonthChange = { direction ->
                        calendar.time = currentDate
                        if (direction == "prev") {
                            calendar.add(Calendar.MONTH, -1)
                        } else {
                            calendar.add(Calendar.MONTH, 1)
                        }
                        currentDate = calendar.time
                    }
                )
            }

            // Today's Events Section
            item {
                TodayEventsSection(
                    selectedDate = selectedDate,
                    events = events,
                    onEventClick = onEventClick
                )
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp),
            containerColor = Blue600,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "일정 추가",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun CalendarCard(
    currentDate: Date,
    selectedDate: Date,
    events: List<Event>,
    onDateSelect: (Date) -> Unit,
    onMonthChange: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = currentDate

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val monthName = SimpleDateFormat("yyyy년 M월", Locale.KOREAN).format(currentDate)

    // Get days in month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp, 20.dp)
        ) {
            // Month Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gray900,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onMonthChange("prev") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "이전 달",
                            tint = Gray700
                        )
                    }
                    IconButton(
                        onClick = { onMonthChange("next") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "다음 달",
                            tint = Gray700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Day of week headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
                daysOfWeek.forEachIndexed { index, day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (index) {
                            0 -> Red400
                            6 -> Blue600
                            else -> Gray500
                        },
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar Grid
            val totalCells = (firstDayOfWeek + daysInMonth).let { (it + 6) / 7 * 7 }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (week in 0 until (totalCells / 7)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (dayOfWeek in 0..6) {
                            val cellIndex = week * 7 + dayOfWeek
                            val dayNumber = cellIndex - firstDayOfWeek + 1

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            ) {
                                if (dayNumber in 1..daysInMonth) {
                                    CalendarDay(
                                        day = dayNumber,
                                        year = year,
                                        month = month,
                                        selectedDate = selectedDate,
                                        hasEvent = hasEventOnDate(events, year, month, dayNumber),
                                        onDateSelect = onDateSelect
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    year: Int,
    month: Int,
    selectedDate: Date,
    hasEvent: Boolean,
    onDateSelect: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()

    calendar.set(year, month, day, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val date = calendar.time

    val isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

    val selectedCal = Calendar.getInstance()
    selectedCal.time = selectedDate
    val isSelected = calendar.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
            calendar.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isToday -> Blue600
                    isSelected -> Blue100
                    else -> Color.Transparent
                }
            )
            .clickable { onDateSelect(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isToday -> Color.White
                    isSelected -> Blue600
                    else -> Gray900
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Medium
            )

            if (hasEvent && !isToday) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Blue600 else Blue600)
                )
            }
        }
    }
}

@Composable
private fun TodayEventsSection(
    selectedDate: Date,
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    val selectedCal = Calendar.getInstance()
    selectedCal.time = selectedDate

    val todayEvents = events.filter { event ->
        val eventCal = Calendar.getInstance()
        eventCal.time = event.date
        eventCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                eventCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                eventCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)
    }.sortedBy { it.startTime }

    val dateFormat = SimpleDateFormat("M월 d일", Locale.KOREAN)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateFormat.format(selectedDate),
                style = MaterialTheme.typography.titleLarge,
                color = Gray900,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${todayEvents.size}개의 일정",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (todayEvents.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp
            ) {
                Box(
                    modifier = Modifier.padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "예정된 일정이 없습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray300
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                todayEvents.forEach { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                color = Gray900,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${event.startTime} - ${event.endTime}",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun hasEventOnDate(events: List<Event>, year: Int, month: Int, day: Int): Boolean {
    val calendar = Calendar.getInstance()
    return events.any { event ->
        calendar.time = event.date
        calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month &&
                calendar.get(Calendar.DAY_OF_MONTH) == day
    }
}
