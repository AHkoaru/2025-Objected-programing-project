package com.example.calendar.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendar.database.AppDatabase
import com.example.calendar.models.Event
import com.example.calendar.notifications.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TabScreen {
    CALENDAR, LIST, INSIGHT, AI
}

class CalendarViewModel(private val application: Application) : AndroidViewModel(application) {

    private val eventDao = AppDatabase.getDatabase(application).eventDao()

    val events: StateFlow<List<Event>> = eventDao.getAllEvents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private var _activeTab by mutableStateOf(TabScreen.CALENDAR)
    val activeTab: TabScreen
        get() = _activeTab

    private var _selectedDate by mutableStateOf(Date())
    val selectedDate: Date
        get() = _selectedDate

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

    fun addEvent(event: Event) = viewModelScope.launch {
        eventDao.insertEvent(event)
        // TODO: The notification for a new event might not be scheduled correctly as the ID is generated upon insertion.
        // A possible solution is to retrieve the event after insertion to get the generated ID.
        scheduleEventNotification(event)
    }

    fun updateEvent(updatedEvent: Event) = viewModelScope.launch {
        eventDao.updateEvent(updatedEvent)
        scheduleEventNotification(updatedEvent)
    }

    fun deleteEvent(eventId: Int) = viewModelScope.launch {
        val event = events.value.find { it.id == eventId }
        if (event != null) {
            cancelEventNotification(event)
            eventDao.deleteEvent(event)
        }
    }

    fun editFromDetail(event: Event) {
        selectedEvent = event
        detailDialogOpen = false
        editDialogOpen = true
    }

    private fun scheduleEventNotification(event: Event) {
        val reminderTime = getReminderTime(event.date, event.startTime)
        // Schedule notification only for future events
        if (reminderTime > System.currentTimeMillis()) {
            NotificationScheduler.scheduleNotification(application, event, reminderTime)
        }
    }

    private fun cancelEventNotification(event: Event) {
        NotificationScheduler.cancelNotification(application, event)
    }

    private fun getReminderTime(date: Date, startTime: String): Long {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        val eventTime = try {
            timeFormat.parse(startTime)
        } catch (e: Exception) {
            null
        }

        val eventCalendar = Calendar.getInstance().apply {
            time = date
            eventTime?.let {
                val cal = Calendar.getInstance().apply { time = it }
                set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            add(Calendar.MINUTE, -10) // 10-minute reminder
        }

        return eventCalendar.timeInMillis
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
