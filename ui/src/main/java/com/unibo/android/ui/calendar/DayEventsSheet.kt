package com.unibo.android.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.EventCard
import com.unibo.android.ui.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEventsSheet(
    selectedDay: Long,
    events: List<EventModel>,
    tags: List<TagModel>,
    onEventClick: (EventModel) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                formatDate(selectedDay),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (events.isEmpty()) {
                Text(
                    "Nessun evento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                events.forEach { event ->
                    EventCard(event = event, tags = tags, onClick = { onEventClick(event) })
                }
            }
        }
    }
}
