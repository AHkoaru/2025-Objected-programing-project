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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendar.models.Event
import com.example.calendar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventListPage(
    events: List<Event>,
    onEventClick: (Event) -> Unit,
    onAddEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedEvents = events.groupBy {
        SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREAN).format(it.date)
    }.toSortedMap(compareBy { key ->
        events.find { event ->
            SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREAN).format(event.date) == key
        }?.date
    })

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(bottom = 160.dp),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Stats Card
            item {
                StatsCard(totalEvents = events.size)
            }

            // Event List
            if (groupedEvents.isEmpty()) {
                item {
                    EmptyEventList()
                }
            } else {
                groupedEvents.forEach { (dateKey, dateEvents) ->
                    item {
                        DateSection(
                            dateKey = dateKey,
                            events = dateEvents.sortedBy { it.startTime },
                            onEventClick = onEventClick
                        )
                    }
                }
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
private fun StatsCard(totalEvents: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Blue600,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "전체 일정",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = totalEvents.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyEventList() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier.padding(60.dp, 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "등록된 일정이 없습니다",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray300
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "새로운 일정을 추가해보세요",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray300
                )
            }
        }
    }
}

@Composable
private fun DateSection(
    dateKey: String,
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    val isToday = isToday(events.first().date)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Header
        Row(
            modifier = Modifier.padding(start = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateKey,
                style = MaterialTheme.typography.titleMedium,
                color = Gray900,
                fontWeight = FontWeight.Bold
            )

            if (isToday) {
                Surface(
                    color = Blue100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "오늘",
                        style = MaterialTheme.typography.labelSmall,
                        color = Blue600,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Events for this date
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            events.forEach { event ->
                EventListItem(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun EventListItem(
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                color = Gray900,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${event.startTime} - ${event.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    fontWeight = FontWeight.Medium
                )
            }

            event.location?.let { location ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun isToday(date: Date): Boolean {
    val today = Calendar.getInstance()
    val eventCal = Calendar.getInstance()
    eventCal.time = date

    return today.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == eventCal.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH)
}
