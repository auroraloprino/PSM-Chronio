package com.unibo.android.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.eventSpansDay
import com.unibo.android.ui.utils.formatDayFull
import com.unibo.android.ui.utils.isSameDay
import com.unibo.android.ui.utils.formatTime

private val HOUR_HEIGHT = 64.dp
private val TIME_COL_WIDTH = 52.dp

private const val DEFAULT_TAG_COLOR = "#9E9E9E"

@Composable
fun DayView(
    selectedDay: Long,
    events: List<EventModel>,
    tags: List<TagModel> = emptyList(),
    onEventClick: (EventModel) -> Unit,
    onSlotClick: (hour: Int) -> Unit = {},
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val tagsById = tags.associateBy { it.id }
    val allDayEvents = events.filter { it.endTime - it.startTime >= 24 * 3600_000L }
    val timedEvents = events - allDayEvents.toSet()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrev) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Giorno precedente")
            }
            Text(formatDayFull(selectedDay), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Giorno successivo")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Giornata",
                modifier = Modifier.width(TIME_COL_WIDTH),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                modifier = Modifier.weight(1f).padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                allDayEvents.forEach { event ->
                    val accentColor = event.tagIds.firstOrNull()
                        ?.let { tagsById[it] }
                        ?.let { runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(it.color)) }.getOrNull() }
                        ?: androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(DEFAULT_TAG_COLOR))
                    val onAccent = if (accentColor.luminance() > 0.4f) androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.White
                    Text(
                        event.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(accentColor, MaterialTheme.shapes.extraSmall)
                            .clickable(onClick = { onEventClick(event) })
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = onAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            (0..23).forEach { hour ->
                val hourEvents = timedEvents.filter { event ->
                    val startHour = java.util.Calendar.getInstance()
                        .apply { timeInMillis = event.startTime }.get(java.util.Calendar.HOUR_OF_DAY)
                    if (isSameDay(event.startTime, selectedDay)) {
                        startHour == hour
                    } else if (eventSpansDay(event.startTime, event.endTime, selectedDay)) {
                        hour == 0
                    } else false
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HOUR_HEIGHT)
                        .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        .clickable { onSlotClick(hour) }
                ) {
                    Box(
                        modifier = Modifier.width(TIME_COL_WIDTH).padding(top = 4.dp, end = 8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            "%02d:00".format(hour),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        hourEvents.forEach { event ->
                            val accentColor = event.tagIds.firstOrNull()
                                ?.let { tagsById[it] }
                                ?.let { runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(it.color)) }.getOrNull() }
                                ?: androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(DEFAULT_TAG_COLOR))
                            val onAccent = if (accentColor.luminance() > 0.4f) androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.White
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(accentColor, MaterialTheme.shapes.small)
                                    .clickable(onClick = { onEventClick(event) })
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    event.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = onAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${formatTime(event.startTime)} – ${formatTime(event.endTime)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = onAccent.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
