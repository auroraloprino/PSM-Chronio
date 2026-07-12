package com.unibo.android.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.EventCard
import com.unibo.android.ui.utils.formatDate

@Composable
fun DayView(
    selectedDay: Long,
    events: List<EventModel>,
    tags: List<TagModel>,
    onEventClick: (EventModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            formatDate(selectedDay),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (events.isEmpty()) {
            Text(
                "Nessun evento",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events) { event ->
                    val eventTags = tags.filter { tag -> event.id > 0 && tag.id > 0 }
                    EventCard(event = event, tags = eventTags, onClick = { onEventClick(event) })
                }
            }
        }
    }
}
