package com.unibo.android.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unibo.android.ui.utils.TagFilterRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(vm: CalendarViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Calendar") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.selectEvent(null); showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add event")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MonthGrid(
                visibleMonth = state.visibleMonth,
                selectedDay = state.selectedDay,
                hasEvents = { vm.hasEvents(it) },
                onDayClick = { vm.selectDay(it) },
                onPrev = { vm.prevMonth() },
                onNext = { vm.nextMonth() }
            )

            TagFilterRow(
                tags = state.tags,
                activeFilters = state.activeFilters,
                onToggle = { vm.toggleFilter(it) }
            )

            DayView(
                selectedDay = state.selectedDay,
                events = vm.eventsForSelectedDay(),
                tags = state.tags,
                onEventClick = { vm.selectEvent(it); showForm = true }
            )
        }

        if (showForm) {
            ModalBottomSheet(
                onDismissRequest = { showForm = false },
                sheetState = sheetState
            ) {
                EventForm(
                    initial = state.selectedEvent,
                    tags = state.tags,
                    selectedDay = state.selectedDay,
                    onSave = { event, tagIds ->
                        if (state.selectedEvent == null) vm.saveEvent(event, tagIds)
                        else vm.updateEvent(event, tagIds)
                        showForm = false
                    },
                    onDelete = { event ->
                        vm.deleteEvent(event)
                        showForm = false
                    },
                    onDismiss = { showForm = false }
                )
            }
        }
    }
}
