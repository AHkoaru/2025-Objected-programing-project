package com.example.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendar.ui.components.*
import com.example.calendar.ui.screens.*
import com.example.calendar.ui.theme.BackgroundGray
import com.example.calendar.ui.theme.CalendarAppTheme
import com.example.calendar.viewmodels.CalendarViewModel
import com.example.calendar.viewmodels.TabScreen

@Composable
fun CalendarApp() {
    CalendarAppTheme {
        val viewModel: CalendarViewModel = viewModel()

        Scaffold(
            topBar = {
                TossHeader(title = viewModel.getHeaderTitle())
            },
            bottomBar = {
                BottomTabBar(
                    activeTab = viewModel.activeTab,
                    onTabChange = { viewModel.setActiveTab(it) }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundGray)
            ) {
                when (viewModel.activeTab) {
                    TabScreen.CALENDAR -> CalendarPage(
                        events = viewModel.events,
                        selectedDate = viewModel.selectedDate,
                        onDateSelect = { viewModel.setSelectedDate(it) },
                        onAddEvent = { viewModel.openAddDialog() },
                        onEventClick = { viewModel.openDetailDialog(it) }
                    )

                    TabScreen.LIST -> EventListPage(
                        events = viewModel.events,
                        onEventClick = { viewModel.openDetailDialog(it) },
                        onAddEvent = { viewModel.openAddDialog() }
                    )

                    TabScreen.INSIGHT -> InsightPage(
                        events = viewModel.events
                    )

                    TabScreen.AI -> AISearchPage(
                        events = viewModel.events,
                        onEventClick = { viewModel.openDetailDialog(it) }
                    )
                }
            }

            // Dialogs
            AddEventDialog(
                open = viewModel.addDialogOpen,
                selectedDate = viewModel.selectedDate,
                onClose = { viewModel.closeAddDialog() },
                onSave = { event ->
                    viewModel.addEvent(event)
                    viewModel.closeAddDialog()
                }
            )

            EditEventDialog(
                open = viewModel.editDialogOpen,
                event = viewModel.selectedEvent,
                onClose = { viewModel.closeEditDialog() },
                onSave = { event ->
                    viewModel.updateEvent(event)
                    viewModel.closeEditDialog()
                }
            )

            EventDetailDialog(
                open = viewModel.detailDialogOpen,
                event = viewModel.selectedEvent,
                onClose = { viewModel.closeDetailDialog() },
                onEdit = { event ->
                    viewModel.editFromDetail(event)
                },
                onDelete = { eventId ->
                    viewModel.deleteEvent(eventId)
                    viewModel.closeDetailDialog()
                }
            )
        }
    }
}
