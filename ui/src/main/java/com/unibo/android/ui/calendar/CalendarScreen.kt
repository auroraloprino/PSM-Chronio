package com.unibo.android.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unibo.android.ui.components.Sidebar
import com.unibo.android.ui.utils.TagFilterRow
import com.unibo.android.ui.utils.addDays
import com.unibo.android.ui.utils.isSameDay
import com.unibo.android.ui.utils.isSameWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(vm: CalendarViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDrawer by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var showDaySheet by remember { mutableStateOf(false) }

    val tabs = listOf("Mese", "Settimana", "Giorno")
    val tabIndex = when (state.calendarView) {
        CalendarView.MONTH -> 0
        CalendarView.WEEK -> 1
        CalendarView.DAY -> 2
    }

    val now = System.currentTimeMillis()
    val todayEvents = state.events.filter { isSameDay(it.startTime, now) }
    val weekEvents = state.events.filter { isSameWeek(it.startTime, now) && !isSameDay(it.startTime, now) && it.startTime > now }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chronio") },
                    navigationIcon = {
                        IconButton(onClick = { showDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { vm.selectEvent(null); showForm = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi evento")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                ScrollableTabRow(selectedTabIndex = tabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = {
                                vm.setView(when (index) {
                                    0 -> CalendarView.MONTH
                                    1 -> CalendarView.WEEK
                                    else -> CalendarView.DAY
                                })
                            },
                            text = { Text(title) }
                        )
                    }
                }

                when (state.calendarView) {
                    CalendarView.MONTH -> {
                        MonthGrid(
                            visibleMonth = state.visibleMonth,
                            selectedDay = state.selectedDay,
                            hasEvents = { vm.hasEvents(it) },
                            onDayClick = { vm.selectDay(it) },
                            onDayDoubleClick = { vm.selectDay(it); showDaySheet = true },
                            onPrev = { vm.prevMonth() },
                            onNext = { vm.nextMonth() }
                        )
                        TagFilterRow(
                            tags = state.tags,
                            activeFilters = state.activeFilters,
                            onToggle = { vm.toggleFilter(it) }
                        )
                        EventListView(
                            selectedDay = state.selectedDay,
                            events = vm.eventsForSelectedDay(),
                            tags = state.tags,
                            onEventClick = { vm.selectEvent(it); showForm = true },
                            todayEvents = todayEvents,
                            weekEvents = weekEvents
                        )
                    }
                    CalendarView.WEEK -> {
                        WeekView(
                            visibleWeek = state.visibleWeek,
                            selectedDay = state.selectedDay,
                            events = vm.eventsForWeek(state.visibleWeek),
                            onDayClick = { vm.selectDay(it) },
                            onEventClick = { vm.selectEvent(it); showForm = true },
                            onPrev = { vm.prevWeek() },
                            onNext = { vm.nextWeek() }
                        )
                    }
                    CalendarView.DAY -> {
                        DayView(
                            selectedDay = state.selectedDay,
                            events = vm.eventsForSelectedDay(),
                            onEventClick = { vm.selectEvent(it); showForm = true },
                            onPrev = { vm.selectDay(addDays(state.selectedDay, -1)) },
                            onNext = { vm.selectDay(addDays(state.selectedDay, 1)) }
                        )
                    }
                }
            }

            if (showForm) {
                ModalBottomSheet(onDismissRequest = { showForm = false }, sheetState = sheetState) {
                    EventForm(
                        initial = state.selectedEvent,
                        tags = state.tags,
                        selectedDay = state.selectedDay,
                        onSave = { event, tagIds ->
                            if (state.selectedEvent == null) vm.saveEvent(event, tagIds)
                            else vm.updateEvent(event, tagIds)
                            showForm = false
                        },
                        onDelete = { vm.deleteEvent(it); showForm = false },
                        onDismiss = { showForm = false }
                    )
                }
            }

            if (showDaySheet) {
                DayEventsSheet(
                    selectedDay = state.selectedDay,
                    events = vm.eventsForDay(state.selectedDay),
                    tags = state.tags,
                    onEventClick = { vm.selectEvent(it); showDaySheet = false; showForm = true },
                    onDismiss = { showDaySheet = false }
                )
            }
        }

        Sidebar(visible = showDrawer, onClose = { showDrawer = false })
    }
}
