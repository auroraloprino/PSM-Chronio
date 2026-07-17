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
import com.unibo.android.ui.utils.EventCard
import com.unibo.android.ui.utils.formatDate
import com.unibo.android.ui.utils.isSameDay
import com.unibo.android.ui.utils.isSameWeek

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
    val isSelectedToday = isSameDay(selectedDay, now)
    val isSelectedThisWeek = isSameWeek(selectedDay, now) && !isSelectedToday
    val tagsById = tags.associateBy { it.id }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (todayEvents.isNotEmpty()) {
            item { SectionHeader("Oggi") }
            items(todayEvents) { event ->
                EventCard(
                    event = event,
                    tags = event.tagIds.mapNotNull { tagsById[it] },
                    onClick = { onEventClick(event) }
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        }

        if (weekEvents.isNotEmpty()) {
            item { SectionHeader("Questa settimana") }
            items(weekEvents) { event ->
                EventCard(
                    event = event,
                    tags = event.tagIds.mapNotNull { tagsById[it] },
                    onClick = { onEventClick(event) }
                )
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
