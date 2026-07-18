package com.unibo.android.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.eventSpansDay
import com.unibo.android.ui.utils.EventCard
import com.unibo.android.ui.utils.formatDate
import com.unibo.android.ui.utils.isSameDay
import com.unibo.android.ui.utils.isSameWeek
import com.unibo.android.ui.utils.MILLIS_PER_DAY
import com.unibo.android.ui.utils.startOfDay

@Composable
fun EventListView(
    selectedDay: Long,
    events: List<EventModel>,
    tags: List<TagModel>,
    onEventClick: (EventModel) -> Unit,
    todayEvents: List<EventModel> = emptyList(),
    weekEvents: List<EventModel> = emptyList()
) {
    val now = System.currentTimeMillis()
    val todayStart = startOfDay(now)
    val isSelectedToday = isSameDay(selectedDay, now)
    val isSelectedThisWeek = isSameWeek(selectedDay, now) && !isSelectedToday
    val tagsById = tags.associateBy { it.id }

    fun isAllDay(e: EventModel) = e.allDay
    fun timeOfDay(ms: Long): Int {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = ms }
        return cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
    }
    fun sortedEvents(list: List<EventModel>) = list.sortedWith(
        compareByDescending<EventModel> { isAllDay(it) }.thenBy { timeOfDay(it.startTime) }
    )

    val weekByDay: List<Pair<Long, List<EventModel>>> = (1..6).mapNotNull { offset ->
        val dayMs = todayStart + offset * MILLIS_PER_DAY
        val dayEvents = sortedEvents(weekEvents.filter { eventSpansDay(it.startTime, it.endTime, dayMs) })
        if (dayEvents.isEmpty()) null else dayMs to dayEvents
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (todayEvents.isNotEmpty()) {
            item { SectionHeader("Oggi") }
            items(sortedEvents(todayEvents)) { event ->
                EventCard(
                    event = event,
                    tags = event.tagIds.mapNotNull { tagsById[it] },
                    onClick = { onEventClick(event) }
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        }

        if (weekByDay.isNotEmpty()) {
            item { SectionHeader("Questa settimana") }
            weekByDay.forEach { (dayMs, dayEvents) ->
                item { DaySubHeader(formatDate(dayMs)) }
                items(dayEvents) { event ->
                    EventCard(
                        event = event,
                        tags = event.tagIds.mapNotNull { tagsById[it] },
                        onClick = { onEventClick(event) }
                    )
                }
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        }

        if (!isSelectedToday && !isSelectedThisWeek) {
            item { SectionHeader(formatDate(selectedDay)) }
            if (events.isEmpty()) {
                item {
                    Text(
                        "Nessun evento",
                        modifier = Modifier.padding(vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(events) { event ->
                    EventCard(
                        event = event,
                        tags = event.tagIds.mapNotNull { tagsById[it] },
                        onClick = { onEventClick(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun DaySubHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp)
    )
}
