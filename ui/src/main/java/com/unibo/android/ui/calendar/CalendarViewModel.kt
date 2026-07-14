package com.unibo.android.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.android.domain.di.UseCasesProvider
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.addMonths
import com.unibo.android.ui.utils.addWeeks
import com.unibo.android.ui.utils.endOfDay
import com.unibo.android.ui.utils.isSameDay
import com.unibo.android.ui.utils.isSameWeek
import com.unibo.android.ui.utils.startOfDay
import com.unibo.android.ui.utils.startOfWeek
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CalendarView { MONTH, WEEK, DAY }

data class CalendarUiState(
    val events: List<EventModel> = emptyList(),
    val tags: List<TagModel> = emptyList(),
    val visibleMonth: Long = startOfDay(System.currentTimeMillis()),
    val visibleWeek: Long = startOfDay(System.currentTimeMillis()),
    val selectedDay: Long = startOfDay(System.currentTimeMillis()),
    val activeFilters: Set<Long> = emptySet(),
    val selectedEvent: EventModel? = null,
    val calendarView: CalendarView = CalendarView.MONTH
)

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    events = UseCasesProvider.getEventsUseCase(),
                    tags = UseCasesProvider.getTagsUseCase()
                )
            }
        }
    }

    fun selectDay(ms: Long) = _uiState.update { it.copy(selectedDay = startOfDay(ms)) }

    fun selectEvent(event: EventModel?) = _uiState.update { it.copy(selectedEvent = event) }

    fun nextMonth() = _uiState.update { it.copy(visibleMonth = addMonths(it.visibleMonth, 1)) }

    fun prevMonth() = _uiState.update { it.copy(visibleMonth = addMonths(it.visibleMonth, -1)) }

    fun nextWeek() = _uiState.update { it.copy(visibleWeek = addWeeks(it.visibleWeek, 1)) }

    fun prevWeek() = _uiState.update { it.copy(visibleWeek = addWeeks(it.visibleWeek, -1)) }

    fun setView(view: CalendarView) = _uiState.update { it.copy(calendarView = view) }

    fun toggleFilter(tagId: Long) {
        _uiState.update {
            val updated = it.activeFilters.toMutableSet()
            if (updated.contains(tagId)) updated.remove(tagId) else updated.add(tagId)
            it.copy(activeFilters = updated)
        }
    }

    fun eventsForDay(dayMs: Long): List<EventModel> {
        val state = _uiState.value
        val filtered = if (state.activeFilters.isEmpty()) state.events
        else state.events.filter { event ->
            UseCasesProvider.getTagsUseCase.let { state.activeFilters.any { _ -> true } }
            true
        }
        return filtered.filter { isSameDay(it.startTime, dayMs) }
    }

    fun eventsForSelectedDay(): List<EventModel> = eventsForDay(_uiState.value.selectedDay)

    fun eventsForWeek(weekMs: Long): List<EventModel> {
        val state = _uiState.value
        return state.events.filter { isSameWeek(it.startTime, weekMs) }
    }

    fun hasEvents(dayMs: Long): Boolean = _uiState.value.events.any { isSameDay(it.startTime, dayMs) }

    fun saveEvent(event: EventModel, tagIds: List<Long>) {
        viewModelScope.launch {
            UseCasesProvider.saveEventUseCase(event, tagIds)
            loadData()
        }
    }

    fun updateEvent(event: EventModel, tagIds: List<Long>) {
        viewModelScope.launch {
            UseCasesProvider.updateEventUseCase(event, tagIds)
            loadData()
        }
    }

    fun deleteEvent(event: EventModel) {
        viewModelScope.launch {
            UseCasesProvider.deleteEventUseCase(event)
            _uiState.update { it.copy(selectedEvent = null) }
            loadData()
        }
    }
}
