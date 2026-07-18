package com.unibo.android.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.HolidayModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.domain.models.WeatherModel
import com.unibo.android.ui.utils.EventCard
import com.unibo.android.ui.utils.formatDate

private fun weatherIcon(code: Int): String = when (code) {
    0 -> "☀️"
    1, 2 -> "🌤️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    51, 53, 55, 61, 63, 65 -> "🌧️"
    71, 73, 75, 77 -> "❄️"
    80, 81, 82 -> "🌦️"
    95, 96, 99 -> "⛈️"
    else -> "🌡️"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEventsSheet(
    visible: Boolean,
    selectedDay: Long,
    events: List<EventModel>,
    tags: List<TagModel>,
    weather: WeatherModel?,
    holiday: HolidayModel?,
    onEventClick: (EventModel) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    val tagsById = tags.associateBy { it.id }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(formatDate(selectedDay), style = MaterialTheme.typography.titleMedium)
                if (weather != null) {
                    Spacer(Modifier.width(12.dp))
                    Text(weatherIcon(weather.weatherCode), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${weather.tempMax.toInt()}° / ${weather.tempMin.toInt()}°",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (holiday != null) {
                Text(
                    "🎉 ${holiday.localName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (events.isEmpty()) {
                Text(
                    "Nessun evento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
}
