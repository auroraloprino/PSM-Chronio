package com.unibo.android.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.android.domain.di.UseCasesProvider
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.HolidayModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.domain.models.WeatherModel
import com.unibo.android.ui.utils.startOfMonth
import com.unibo.android.ui.utils.endOfMonth
import com.unibo.android.ui.utils.addMonths
import com.unibo.android.ui.utils.addWeeks
import com.unibo.android.ui.utils.endOfDay
import com.unibo.android.ui.utils.endOfMonth
import com.unibo.android.ui.utils.eventSpansDay
import com.unibo.android.ui.utils.isSameDay
import com.unibo.android.ui.utils.isSameWeek
import com.unibo.android.ui.utils.startOfDay
import com.unibo.android.ui.utils.startOfMonth
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
    val calendarView: CalendarView = CalendarView.MONTH,
    val weatherByDay: Map<String, WeatherModel> = emptyMap(),
    val holidaysByDay: Map<String, HolidayModel> = emptyMap()
)

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init { loadData(); loadWeather(); loadHolidays() }

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

    fun selectDay(ms: Long) = _uiState.update {
        it.copy(
            selectedDay = startOfDay(ms),
            visibleWeek = startOfDay(ms)
        )
    }

    fun selectEvent(event: EventModel?) = _uiState.update { it.copy(selectedEvent = event) }

    fun loadHolidays() {
        viewModelScope.launch {
            runCatching {
                val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                val holidays = UseCasesProvider.getHolidaysUseCase(year)
                _uiState.update { it.copy(holidaysByDay = holidays.associateBy { h -> h.date }) }
                val existingTags = UseCasesProvider.getTagsUseCase()
                if (existingTags.none { it.isSystem }) {
                    UseCasesProvider.saveTagUseCase(
                        TagModel(name = "Festivit\u00e0", color = "#E53935", isSystem = true)
                    )
                    loadData()
                }
            }
        }
    }

    fun nextMonth() {
        _uiState.update { it.copy(visibleMonth = addMonths(it.visibleMonth, 1)) }
        loadWeather()
    }

    fun prevMonth() {
        _uiState.update { it.copy(visibleMonth = addMonths(it.visibleMonth, -1)) }
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            runCatching {
                val month = _uiState.value.visibleMonth
                val forecast = UseCasesProvider.getWeatherUseCase(0.0, 0.0, startOfMonth(month), endOfMonth(month))
                _uiState.update { it.copy(weatherByDay = forecast.associateBy { w -> w.date }) }
            }
        }
    }

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
        val filtered = applyFilters(state)
        return filtered.filter { eventSpansDay(it.startTime, it.endTime, dayMs) }
    }

    fun eventsForSelectedDay(): List<EventModel> = eventsForDay(_uiState.value.selectedDay)

    fun eventsForWeek(weekMs: Long): List<EventModel> {
        val state = _uiState.value
        val filtered = applyFilters(state)
        val weekStart = startOfWeek(weekMs)
        val weekEnd = weekStart + 7 * 24 * 3600_000L
        return filtered.filter { it.startTime < weekEnd && it.endTime > weekStart }
    }

    fun hasEvents(dayMs: Long): Boolean {
        val state = _uiState.value
        val filtered = applyFilters(state)
        return filtered.any { eventSpansDay(it.startTime, it.endTime, dayMs) }
    }

    private fun applyFilters(state: CalendarUiState): List<EventModel> {
        if (state.activeFilters.isEmpty()) return state.events
        val noTagActive = NO_TAG_FILTER_ID in state.activeFilters
        val tagFilters = state.activeFilters - NO_TAG_FILTER_ID
        return state.events.filter { event ->
            (noTagActive && event.tagIds.isEmpty()) ||
            tagFilters.any { it in event.tagIds }
        }
    }

    companion object {
        const val NO_TAG_FILTER_ID = -1L
    }

    fun saveEvent(event: EventModel, tagIds: List<Long>) {
        viewModelScope.launch {
            val id = UseCasesProvider.saveEventUseCase(event, tagIds)
            UseCasesProvider.reminderScheduler?.schedule(event.copy(id = id))
            loadData()
        }
    }

    fun updateEvent(event: EventModel, tagIds: List<Long>) {
        viewModelScope.launch {
            UseCasesProvider.reminderScheduler?.cancel(event.id)
            UseCasesProvider.updateEventUseCase(event, tagIds)
            UseCasesProvider.reminderScheduler?.schedule(event)
            loadData()
        }
    }

    fun saveTag(tag: TagModel) {
        viewModelScope.launch {
            UseCasesProvider.saveTagUseCase(tag)
            loadData()
        }
    }

    fun updateTag(tag: TagModel) {
        viewModelScope.launch {
            UseCasesProvider.saveTagUseCase(tag)
            loadData()
        }
    }

    fun deleteTag(tag: TagModel) {
        viewModelScope.launch {
            UseCasesProvider.deleteTagUseCase(tag)
            loadData()
        }
    }

    fun deleteEvent(event: EventModel) {
        viewModelScope.launch {
            UseCasesProvider.reminderScheduler?.cancel(event.id)
            UseCasesProvider.deleteEventUseCase(event)
            _uiState.update { it.copy(selectedEvent = null) }
            loadData()
        }
    }
}
