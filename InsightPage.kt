package com.example.calendar.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.model.Event
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun InsightPage(events: List<Event>) {
    // 통계 계산
    val stats = remember(events) { calculateStats(events) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            HeaderSection()
        }

        // Stats Grid
        item {
            StatsGrid(stats)
        }

        // Day of Week Chart
        if (events.isNotEmpty()) {
            item {
                DayOfWeekChart(stats.dayOfWeekData, stats.busiestDay)
            }
        }

        // Time Distribution
        if (events.isNotEmpty()) {
            item {
                TimeDistribution(stats.timeSlots)
            }
        }

        // Top Locations
        if (stats.topLocations.isNotEmpty()) {
            item {
                TopLocations(stats.topLocations)
            }
        }

        // Empty State
        if (events.isEmpty()) {
            item {
                EmptyState()
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3182F6),
                        Color(0xFF4DABF7)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "일정 인사이트",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = "나의 일정 패턴을 한눈에 확인하세요",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun StatsGrid(stats: EventStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                value = stats.totalEvents.toString(),
                label = "전체 일정",
                iconColor = Color(0xFF3182F6),
                backgroundColor = Color(0xFFE8F3FF)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                value = stats.thisWeekEvents.toString(),
                label = "이번 주 일정",
                iconColor = Color(0xFFFF8C42),
                backgroundColor = Color(0xFFFFF3E8)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.TrendingUp,
                value = stats.upcomingEvents.toString(),
                label = "다가오는 일정",
                iconColor = Color(0xFF20C997),
                backgroundColor = Color(0xFFE8FFF3)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.BarChart,
                value = stats.avgDurationText,
                label = "평균 일정 시간",
                iconColor = Color(0xFF9775FA),
                backgroundColor = Color(0xFFF3E8FF)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    iconColor: Color,
    backgroundColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color(0xFF868E96),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DayOfWeekChart(dayData: List<DayOfWeekData>, busiestDay: DayOfWeekData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp, 20.dp)
        ) {
            Text(
                text = "요일별 일정 분포",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (busiestDay.count > 0)
                    "가장 바쁜 요일은 ${busiestDay.name}요일입니다 (${busiestDay.count}개)"
                else "일정을 추가해보세요",
                fontSize = 13.sp,
                color = Color(0xFF868E96)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Simple Bar Chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxCount = dayData.maxOfOrNull { it.count } ?: 1
                dayData.forEach { day ->
                    BarItem(
                        label = day.name,
                        count = day.count,
                        maxCount = maxCount,
                        color = day.color
                    )
                }
            }
        }
    }
}

@Composable
private fun BarItem(label: String, count: Int, maxCount: Int, color: Color) {
    val heightFraction = if (maxCount > 0) count.toFloat() / maxCount else 0f
    val animatedHeight by animateFloatAsState(
        targetValue = heightFraction,
        animationSpec = tween(durationMillis = 500),
        label = "bar_height"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(40.dp)
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(160.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (count > 0) {
                    Text(
                        text = count.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF868E96),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height((160 * animatedHeight).dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(color)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF868E96)
        )
    }
}

@Composable
private fun TimeDistribution(timeSlots: List<TimeSlot>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp, 20.dp)
        ) {
            Text(
                text = "시간대별 일정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "선호하는 시간대를 확인하세요",
                fontSize = 13.sp,
                color = Color(0xFF868E96)
            )
            Spacer(modifier = Modifier.height(20.dp))

            val maxCount = timeSlots.maxOfOrNull { it.count } ?: 1
            timeSlots.forEachIndexed { index, slot ->
                TimeSlotItem(slot, maxCount, index)
                if (index < timeSlots.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TimeSlotItem(slot: TimeSlot, maxCount: Int, index: Int) {
    val percentage = if (maxCount > 0) slot.count.toFloat() / maxCount else 0f
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    val colors = listOf(
        Color(0xFF3182F6),
        Color(0xFF4DABF7),
        Color(0xFF74C0FC)
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = slot.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF191F28)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = slot.range,
                    fontSize = 13.sp,
                    color = Color(0xFF868E96)
                )
            }
            Text(
                text = "${slot.count}개",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3182F6)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF1F3F5))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPercentage)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.getOrElse(index) { colors[0] })
            )
        }
    }
}

@Composable
private fun TopLocations(locations: List<LocationData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp, 20.dp)
        ) {
            Text(
                text = "자주 가는 장소",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Top 3 장소",
                fontSize = 13.sp,
                color = Color(0xFF868E96)
            )
            Spacer(modifier = Modifier.height(20.dp))

            val colors = listOf(
                Color(0xFF3182F6),
                Color(0xFF4DABF7),
                Color(0xFF74C0FC)
            )

            locations.forEachIndexed { index, location ->
                LocationItem(location, index + 1, colors.getOrElse(index) { colors[0] })
                if (index < locations.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun LocationItem(location: LocationData, rank: Int, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = location.location,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF191F28)
                )
            }
            Text(
                text = "${location.count}회",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF868E96)
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F3F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PieChart,
                contentDescription = null,
                tint = Color(0xFFADB5BD),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "아직 일정이 없어요",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF191F28)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "일정을 추가하면\n여기에 인사이트가 표시됩니다",
            fontSize = 14.sp,
            color = Color(0xFF868E96),
            lineHeight = 22.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Data classes
data class EventStats(
    val totalEvents: Int,
    val thisWeekEvents: Int,
    val upcomingEvents: Int,
    val pastEvents: Int,
    val avgDurationText: String,
    val dayOfWeekData: List<DayOfWeekData>,
    val busiestDay: DayOfWeekData,
    val timeSlots: List<TimeSlot>,
    val topLocations: List<LocationData>
)

data class DayOfWeekData(
    val name: String,
    var count: Int,
    val color: Color
)

data class TimeSlot(
    val name: String,
    val range: String,
    var count: Int
)

data class LocationData(
    val location: String,
    val count: Int
)

// Statistics calculation
private fun calculateStats(events: List<Event>): EventStats {
    val totalEvents = events.size

    // This week events
    val today = Calendar.getInstance()
    val startOfWeek = Calendar.getInstance().apply {
        time = today.time
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val endOfWeek = Calendar.getInstance().apply {
        time = startOfWeek.time
        add(Calendar.DAY_OF_WEEK, 7)
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val thisWeekEvents = events.count { event ->
        try {
            val eventDate = dateFormat.parse(event.date)
            eventDate != null && eventDate >= startOfWeek.time && eventDate < endOfWeek.time
        } catch (e: Exception) {
            false
        }
    }

    // Upcoming and past events
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val upcomingEvents = events.count { event ->
        try {
            val eventDate = dateFormat.parse(event.date)
            eventDate != null && eventDate >= todayStart.time
        } catch (e: Exception) {
            false
        }
    }

    val pastEvents = events.count { event ->
        try {
            val eventDate = dateFormat.parse(event.date)
            eventDate != null && eventDate < todayStart.time
        } catch (e: Exception) {
            false
        }
    }

    // Day of week distribution
    val dayOfWeekData = listOf(
        DayOfWeekData("일", 0, Color(0xFFFF6B6B)),
        DayOfWeekData("월", 0, Color(0xFF3182F6)),
        DayOfWeekData("화", 0, Color(0xFF3182F6)),
        DayOfWeekData("수", 0, Color(0xFF3182F6)),
        DayOfWeekData("목", 0, Color(0xFF3182F6)),
        DayOfWeekData("금", 0, Color(0xFF3182F6)),
        DayOfWeekData("토", 0, Color(0xFF4DABF7))
    )

    events.forEach { event ->
        try {
            val eventDate = dateFormat.parse(event.date)
            if (eventDate != null) {
                val cal = Calendar.getInstance().apply { time = eventDate }
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
                dayOfWeekData[dayOfWeek].count++
            }
        } catch (e: Exception) {
            // Skip invalid dates
        }
    }

    val busiestDay = dayOfWeekData.maxByOrNull { it.count } ?: dayOfWeekData[0]

    // Time slots
    val timeSlots = listOf(
        TimeSlot("오전", "6-12시", 0),
        TimeSlot("오후", "12-18시", 0),
        TimeSlot("저녁", "18-24시", 0)
    )

    events.forEach { event ->
        val hour = parseHour(event.startTime)
        if (hour != null) {
            when {
                hour in 6..11 -> timeSlots[0].count++
                hour in 12..17 -> timeSlots[1].count++
                hour >= 18 || hour < 6 -> timeSlots[2].count++
            }
        }
    }

    // Average duration
    var totalMinutes = 0
    var validEvents = 0

    events.forEach { event ->
        val startHour = parseHour(event.startTime)
        val startMin = parseMinute(event.startTime)
        val endHour = parseHour(event.endTime)
        val endMin = parseMinute(event.endTime)

        if (startHour != null && startMin != null && endHour != null && endMin != null) {
            val startTotalMin = startHour * 60 + startMin
            val endTotalMin = endHour * 60 + endMin
            val duration = endTotalMin - startTotalMin

            if (duration > 0) {
                totalMinutes += duration
                validEvents++
            }
        }
    }

    val avgMinutes = if (validEvents > 0) (totalMinutes / validEvents) else 0
    val avgHours = avgMinutes / 60
    val avgMins = avgMinutes % 60
    val avgDurationText = buildString {
        if (avgHours > 0) append("${avgHours}h ")
        append("${avgMins}m")
    }.trim()

    // Top locations
    val locationMap = mutableMapOf<String, Int>()
    events.forEach { event ->
        event.location?.let { loc ->
            locationMap[loc] = (locationMap[loc] ?: 0) + 1
        }
    }

    val topLocations = locationMap.entries
        .sortedByDescending { it.value }
        .take(3)
        .map { LocationData(it.key, it.value) }

    return EventStats(
        totalEvents = totalEvents,
        thisWeekEvents = thisWeekEvents,
        upcomingEvents = upcomingEvents,
        pastEvents = pastEvents,
        avgDurationText = avgDurationText,
        dayOfWeekData = dayOfWeekData,
        busiestDay = busiestDay,
        timeSlots = timeSlots,
        topLocations = topLocations
    )
}

private fun parseHour(time: String): Int? {
    val regex = """(\d+):(\d+)\s*(AM|PM)""".toRegex(RegexOption.IGNORE_CASE)
    val match = regex.find(time) ?: return null
    var hour = match.groupValues[1].toIntOrNull() ?: return null
    val period = match.groupValues[3].uppercase()

    if (period == "PM" && hour != 12) hour += 12
    if (period == "AM" && hour == 12) hour = 0

    return hour
}

private fun parseMinute(time: String): Int? {
    val regex = """(\d+):(\d+)\s*(AM|PM)""".toRegex(RegexOption.IGNORE_CASE)
    val match = regex.find(time) ?: return null
    return match.groupValues[2].toIntOrNull()
}
