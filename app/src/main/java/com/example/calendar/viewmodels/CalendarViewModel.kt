package com.example.calendar.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.calendar.models.Event
import com.example.calendar.notifications.NotificationScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TabScreen {
    CALENDAR, LIST, INSIGHT, AI
}

class CalendarViewModel(private val application: Application) : AndroidViewModel(application) {
    private var _activeTab by mutableStateOf(TabScreen.CALENDAR)
    val activeTab: TabScreen
        get() = _activeTab

    private var _selectedDate by mutableStateOf(Date())
    val selectedDate: Date
        get() = _selectedDate

    var events by mutableStateOf(getSampleEvents())
        private set

    var addDialogOpen by mutableStateOf(false)
        private set

    var editDialogOpen by mutableStateOf(false)
        private set

    var detailDialogOpen by mutableStateOf(false)
        private set

    var selectedEvent by mutableStateOf<Event?>(null)
        private set

    fun setActiveTab(tab: TabScreen) {
        _activeTab = tab
    }

    fun setSelectedDate(date: Date) {
        _selectedDate = date
    }

    fun openAddDialog() {
        addDialogOpen = true
    }

    fun closeAddDialog() {
        addDialogOpen = false
    }

    fun openEditDialog(event: Event) {
        selectedEvent = event
        editDialogOpen = true
    }

    fun closeEditDialog() {
        editDialogOpen = false
    }

    fun openDetailDialog(event: Event) {
        selectedEvent = event
        detailDialogOpen = true
    }

    fun closeDetailDialog() {
        detailDialogOpen = false
    }

    fun addEvent(event: Event) {
        events = events + event
        scheduleEventNotification(event)
    }

    fun updateEvent(updatedEvent: Event) {
        events = events.map { if (it.id == updatedEvent.id) updatedEvent else it }
        scheduleEventNotification(updatedEvent)
    }

    fun deleteEvent(eventId: String) {
        events.find { it.id == eventId }?.let { event ->
            cancelEventNotification(event)
            events = events.filter { it.id != eventId }
        }
    }

    fun editFromDetail(event: Event) {
        selectedEvent = event
        detailDialogOpen = false
        editDialogOpen = true
    }

    private fun scheduleEventNotification(event: Event) {
        val reminderTime = getReminderTime(event.date, event.startTime)
        if (reminderTime > System.currentTimeMillis()) {
            NotificationScheduler.scheduleNotification(application, event, reminderTime)
        }
    }

    private fun cancelEventNotification(event: Event) {
        NotificationScheduler.cancelNotification(application, event)
    }

    private fun getReminderTime(date: Date, startTime: String): Long {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val eventTime = timeFormat.parse(startTime)

        val eventCalendar = Calendar.getInstance().apply {
            time = date
            val cal = Calendar.getInstance().apply { time = eventTime!! }
            set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            add(Calendar.MINUTE, -10) // 10-minute reminder
        }

        return eventCalendar.timeInMillis
    }

    private fun getSampleEvents(): List<Event> {
        val calendar = Calendar.getInstance()
        calendar.set(2025, 10, 6, 0, 0, 0) // November 6, 2025

        return listOf(
            Event(
                id = "1",
                title = "팀 미팅",
                date = calendar.time,
                startTime = "9:00 AM",
                endTime = "10:00 AM",
                location = "회의실 A",
                description = "주간 팀 회의 및 프로젝트 진행 상황 공유"
            ),
            Event(
                id = "2",
                title = "프로젝트 리뷰",
                date = calendar.time,
                startTime = "2:00 PM",
                endTime = "3:30 PM",
                location = "사무실 201호",
                description = "분기별 프로젝트 검토 미팅"
            ),
            Event(
                id = "3",
                title = "고객 프레젠테이션",
                date = calendar.time,
                startTime = "4:00 PM",
                endTime = "5:00 PM",
                description = "Q4 결과 발표"
            ),
            Event(
                id = "4",
                title = "점심 약속",
                date = Calendar.getInstance().apply { set(2025, 10, 8, 0, 0, 0) }.time,
                startTime = "12:00 PM",
                endTime = "1:00 PM",
                location = "이탈리안 레스토랑"
            )
        )
    }

    fun getHeaderTitle(): String {
        return when (activeTab) {
            TabScreen.CALENDAR -> "캘린더"
            TabScreen.LIST -> "일정"
            TabScreen.INSIGHT -> "인사이트"
            TabScreen.AI -> "AI 검색"
        }
    }
}
