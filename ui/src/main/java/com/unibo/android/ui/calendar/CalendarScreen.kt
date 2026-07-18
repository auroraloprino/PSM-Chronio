package com.unibo.android.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.unibo.android.ui.components.Sidebar
import com.unibo.android.ui.utils.TagFilterRow
import com.unibo.android.ui.utils.addDays
import com.unibo.android.ui.utils.eventSpansDay
import com.unibo.android.ui.utils.isSameDay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CalendarScreen(vm: CalendarViewModel = viewModel(), onToggleTheme: () -> Unit = {}, isDark: Boolean = false) {
    val state by vm.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDrawer by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var presetStartTime by remember { mutableStateOf<Long?>(null) }
    var showDaySheet by remember { mutableStateOf(false) }
    var showTagManager by remember { mutableStateOf(false) }
    var tagToEdit by remember { mutableStateOf<com.unibo.android.domain.models.TagModel?>(null) }

    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermission = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(Unit) {
        if (!notificationPermission.status.isGranted) notificationPermission.launchPermissionRequest()
        if (!locationPermission.status.isGranted) locationPermission.launchPermissionRequest()
    }
    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) vm.loadWeather()
    }

    val tabs = listOf("Mese", "Settimana", "Giorno")
    val tabIndex = when (state.calendarView) {
        CalendarView.MONTH -> 0
        CalendarView.WEEK -> 1
        CalendarView.DAY -> 2
    }

    val now = System.currentTimeMillis()
    val filteredEvents = if (state.activeFilters.isEmpty()) state.events
        else state.events.filter { event ->
            val noTagActive = CalendarViewModel.NO_TAG_FILTER_ID in state.activeFilters
            val tagFilters = state.activeFilters - CalendarViewModel.NO_TAG_FILTER_ID
            (noTagActive && event.tagIds.isEmpty()) || tagFilters.any { it in event.tagIds }
        }
    val todayStart = com.unibo.android.ui.utils.startOfDay(now)
    val todayEnd = com.unibo.android.ui.utils.endOfDay(now)
    val weekEnd = todayStart + 7 * 24 * 3600_000L
    val todayEvents = filteredEvents.filter { event ->
        event.startTime <= todayEnd && event.endTime > todayStart
    }
    val todayIds = todayEvents.map { it.id }.toSet()
    val weekEvents = filteredEvents.filter { event ->
        (1..6).any { offset ->
            val dayMs = todayStart + offset * 24 * 3600_000L
            eventSpansDay(event.startTime, event.endTime, dayMs)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chronio") },
                    navigationIcon = {
                        IconButton(onClick = { showDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDark) "Tema chiaro" else "Tema scuro"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
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
                            onNext = { vm.nextMonth() },
                            weatherByDay = state.weatherByDay
                        )
                        TagFilterRow(
                            tags = state.tags,
                            activeFilters = state.activeFilters,
                            onToggle = { vm.toggleFilter(it) },
                            onEdit = { tagToEdit = it; showTagManager = true },
                            onDelete = { vm.deleteTag(it) },
                            onCreateNew = { tagToEdit = null; showTagManager = true },
                            noTagFilterId = CalendarViewModel.NO_TAG_FILTER_ID
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
                            tags = state.tags,
                            onDayClick = { vm.selectDay(it) },
                            onSlotClick = { dayMs, hour ->
                                val cal = Calendar.getInstance().apply {
                                    timeInMillis = dayMs
                                    set(Calendar.HOUR_OF_DAY, hour)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                vm.selectEvent(null)
                                presetStartTime = cal.timeInMillis
                                showForm = true
                            },
                            onEventClick = { vm.selectEvent(it); showForm = true },
                            onPrev = { vm.prevWeek() },
                            onNext = { vm.nextWeek() }
                        )
                    }
                    CalendarView.DAY -> {
                        DayView(
                            selectedDay = state.selectedDay,
                            events = vm.eventsForSelectedDay(),
                            tags = state.tags,
                            onEventClick = { vm.selectEvent(it); showForm = true },
                            onSlotClick = { hour ->
                                val cal = Calendar.getInstance().apply {
                                    timeInMillis = state.selectedDay
                                    set(Calendar.HOUR_OF_DAY, hour)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                vm.selectEvent(null)
                                presetStartTime = cal.timeInMillis
                                showForm = true
                            },
                            onPrev = { vm.selectDay(addDays(state.selectedDay, -1)) },
                            onNext = { vm.selectDay(addDays(state.selectedDay, 1)) }
                        )
                    }
                }
            }

            if (showForm) {
                ModalBottomSheet(onDismissRequest = { showForm = false; presetStartTime = null }, sheetState = sheetState) {
                    EventForm(
                        initial = state.selectedEvent,
                        tags = state.tags,
                        selectedDay = state.selectedDay,
                        presetStartTime = presetStartTime,
                        onSave = { event, tagIds ->
                            if (state.selectedEvent == null) vm.saveEvent(event, tagIds)
                            else vm.updateEvent(event, tagIds)
                            showForm = false; presetStartTime = null
                        },
                        onDelete = { vm.deleteEvent(it); showForm = false; presetStartTime = null },
                        onDismiss = { showForm = false; presetStartTime = null }
                    )
                }
            }

            DayEventsSheet(
                visible = showDaySheet,
                selectedDay = state.selectedDay,
                events = vm.eventsForDay(state.selectedDay),
                tags = state.tags,
                weather = state.weatherByDay[java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(state.selectedDay)],
                onEventClick = { vm.selectEvent(it); showDaySheet = false; showForm = true },
                onDismiss = { showDaySheet = false }
            )
        }

        Sidebar(visible = showDrawer, onClose = { showDrawer = false }, onToggleTheme = onToggleTheme, isDark = isDark)

        TagManagerSheet(
            visible = showTagManager,
            tags = state.tags,
            initialTag = tagToEdit,
            onSave = { vm.saveTag(it) },
            onUpdate = { vm.updateTag(it) },
            onDelete = { vm.deleteTag(it) },
            onDismiss = { showTagManager = false; tagToEdit = null }
        )
    }
}
