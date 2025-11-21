package com.example.calendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendar.models.Event
import com.example.calendar.ui.theme.*
import java.util.*

@Composable
fun InsightPage(
    events: List<Event>,
    modifier: Modifier = Modifier
) {
    val stats = calculateStats(events)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Blue600,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Blue600, Blue400)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "일정 인사이트",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "나의 일정 패턴을 한눈에 확인하세요",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Stats Grid
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CalendarMonth,
                        iconColor = Blue600,
                        iconBackground = Blue100,
                        value = stats.totalEvents.toString(),
                        label = "전체 일정"
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccessTime,
                        iconColor = Orange400,
                        iconBackground = Color(0xFFFFF3E8),
                        value = stats.thisWeekEvents.toString(),
                        label = "이번 주 일정"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.TrendingUp,
                        iconColor = Green400,
                        iconBackground = Color(0xFFE8FFF3),
                        value = stats.upcomingEvents.toString(),
                        label = "다가오는 일정"
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.BarChart,
                        iconColor = Purple400,
                        iconBackground = Color(0xFFF3E8FF),
                        value = stats.avgDuration,
                        label = "평균 일정 시간"
                    )
                }
            }
        }

        // Day of Week Distribution
        item {
            InsightCard(
                title = "요일별 일정 분포",
                subtitle = if (stats.busiestDay.isNotEmpty()) {
                    "가장 바쁜 요일은 ${stats.busiestDay}입니다"
                } else {
                    "일정을 추가해보세요"
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    stats.dayOfWeekData.forEach { (day, count, color) ->
                        DayOfWeekBar(
                            day = day,
                            count = count,
                            maxCount = stats.dayOfWeekData.maxOfOrNull { it.count } ?: 1,
                            color = color
                        )
                    }
                }
            }
        }

        // Time Distribution
        item {
            InsightCard(
                title = "시간대별 일정",
                subtitle = "선호하는 시간대를 확인하세요"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val colors = listOf(Blue600, Blue400, Color(0xFF74C0FC))
                    stats.timeSlots.forEachIndexed { index, (name, range, count) ->
                        TimeSlotBar(
                            name = name,
                            range = range,
                            count = count,
                            maxCount = stats.timeSlots.maxOfOrNull { it.count } ?: 1,
                            color = colors.getOrNull(index) ?: Blue600
                        )
                    }
                }
            }
        }

        // Top Locations
        if (stats.topLocations.isNotEmpty()) {
            item {
                InsightCard(
                    title = "자주 가는 장소",
                    subtitle = "Top 3 장소"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val colors = listOf(Blue600, Blue400, Color(0xFF74C0FC))
                        stats.topLocations.forEachIndexed { index, (location, count) ->
                            LocationItem(
                                rank = index + 1,
                                location = location,
                                count = count,
                                color = colors.getOrNull(index) ?: Blue600
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    iconBackground: Color,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = iconBackground,
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium,
                color = Gray900,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun InsightCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp, 20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Gray900,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            content()
        }
    }
}

@Composable
private fun DayOfWeekBar(
    day: String,
    count: Int,
    maxCount: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray900,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(40.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .background(Gray100, RoundedCornerShape(4.dp))
        ) {
            val percentage = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(color, RoundedCornerShape(4.dp))
            )
        }

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = Gray700,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
private fun TimeSlotBar(
    name: String,
    range: String,
    count: Int,
    maxCount: Int,
    color: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray900,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = range,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }

            Text(
                text = "${count}개",
                style = MaterialTheme.typography.bodyMedium,
                color = Blue600,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Gray100, RoundedCornerShape(4.dp))
        ) {
            val percentage = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
private fun LocationItem(
    rank: Int,
    location: String,
    count: Int,
    color: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Gray50,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                color = color,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray900,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${count}회",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

data class InsightStats(
    val totalEvents: Int,
    val thisWeekEvents: Int,
    val upcomingEvents: Int,
    val avgDuration: String,
    val busiestDay: String,
    val dayOfWeekData: List<DayData>,
    val timeSlots: List<TimeSlotData>,
    val topLocations: List<LocationData>
)

data class DayData(val day: String, val count: Int, val color: Color)
data class TimeSlotData(val name: String, val range: String, val count: Int)
data class LocationData(val location: String, val count: Int)

private fun calculateStats(events: List<Event>): InsightStats {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()

    // This week events
    val startOfWeek = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val endOfWeek = Calendar.getInstance().apply {
        time = startOfWeek.time
        add(Calendar.DAY_OF_MONTH, 7)
    }

    val thisWeekEvents = events.count { event ->
        event.date >= startOfWeek.time && event.date < endOfWeek.time
    }

    // Upcoming events
    val upcomingEvents = events.count { it.date >= today.time }

    // Day of week distribution
    val dayOfWeekCounts = mutableMapOf<Int, Int>()
    events.forEach { event ->
        calendar.time = event.date
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        dayOfWeekCounts[dayOfWeek] = (dayOfWeekCounts[dayOfWeek] ?: 0) + 1
    }

    val dayNames = listOf("일", "월", "화", "수", "목", "금", "토")
    val dayColors = listOf(Red400, Blue600, Blue600, Blue600, Blue600, Blue600, Blue400)
    val dayOfWeekData = (1..7).map { day ->
        DayData(dayNames[day - 1], dayOfWeekCounts[day] ?: 0, dayColors[day - 1])
    }

    val busiestDay = dayOfWeekCounts.maxByOrNull { it.value }?.let {
        dayNames[it.key - 1] + "요일"
    } ?: ""

    // Time slots
    val morningCount = events.count { event ->
        val hour = parseHour(event.startTime)
        hour in 6..11
    }
    val afternoonCount = events.count { event ->
        val hour = parseHour(event.startTime)
        hour in 12..17
    }
    val eveningCount = events.count { event ->
        val hour = parseHour(event.startTime)
        hour >= 18 || hour < 6
    }

    val timeSlots = listOf(
        TimeSlotData("오전", "6-12시", morningCount),
        TimeSlotData("오후", "12-18시", afternoonCount),
        TimeSlotData("저녁", "18-24시", eveningCount)
    )

    // Top locations
    val locationCounts = events.mapNotNull { it.location }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(3)
        .map { LocationData(it.key, it.value) }

    // Average duration
    var totalMinutes = 0
    var validEvents = 0
    events.forEach { event ->
        val startHour = parseHour(event.startTime)
        val endHour = parseHour(event.endTime)
        if (startHour >= 0 && endHour >= 0) {
            val duration = if (endHour > startHour) {
                (endHour - startHour) * 60
            } else {
                0
            }
            if (duration > 0) {
                totalMinutes += duration
                validEvents++
            }
        }
    }

    val avgMinutes = if (validEvents > 0) totalMinutes / validEvents else 0
    val avgHours = avgMinutes / 60
    val avgMins = avgMinutes % 60
    val avgDuration = if (avgHours > 0) "${avgHours}h ${avgMins}m" else "${avgMins}m"

    return InsightStats(
        totalEvents = events.size,
        thisWeekEvents = thisWeekEvents,
        upcomingEvents = upcomingEvents,
        avgDuration = avgDuration,
        busiestDay = busiestDay,
        dayOfWeekData = dayOfWeekData,
        timeSlots = timeSlots,
        topLocations = locationCounts
    )
}

private fun parseHour(time: String): Int {
    val match = Regex("""(\d+):(\d+)\s*(AM|PM)""").find(time) ?: return -1
    var hour = match.groupValues[1].toIntOrNull() ?: return -1
    val period = match.groupValues[3]

    if (period == "PM" && hour != 12) hour += 12
    if (period == "AM" && hour == 12) hour = 0

    return hour
}
