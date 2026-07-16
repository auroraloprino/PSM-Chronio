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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.formatDayShort
import com.unibo.android.ui.utils.formatWeekRange
import com.unibo.android.ui.utils.isSameDay
import com.unibo.android.ui.utils.startOfWeek
import java.util.Calendar

private val HOUR_HEIGHT = 56.dp
private val TIME_COL_WIDTH = 56.dp

@Composable
fun WeekView(
    visibleWeek: Long,
    selectedDay: Long,
    events: List<EventModel>,
    tags: List<TagModel> = emptyList(),
    onDayClick: (Long) -> Unit,
    onEventClick: (EventModel) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val tagsById = tags.associateBy { it.id }
    val weekStart = startOfWeek(visibleWeek)
    val days = (0..6).map { offset ->
        Calendar.getInstance().apply {
            timeInMillis = weekStart
            add(Calendar.DAY_OF_YEAR, offset)
        }.timeInMillis
    }
    val today = System.currentTimeMillis()

    val allDayEvents = events.filter {
        val duration = it.endTime - it.startTime
        duration >= 24 * 3600_000L
    }
    val timedEvents = events - allDayEvents.toSet()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrev) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Settimana precedente")
            }
            Text(formatWeekRange(visibleWeek), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Settimana successiva")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(TIME_COL_WIDTH))
            days.forEach { dayMs ->
                val isToday = isSameDay(dayMs, today)
                val isSelected = isSameDay(dayMs, selectedDay)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDayClick(dayMs) }
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        formatDayShort(dayMs),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(
                modifier = Modifier.width(TIME_COL_WIDTH).padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Giorno", style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            }
            days.forEach { dayMs ->
                val dayAllDay = allDayEvents.filter { isSameDay(it.startTime, dayMs) }
                Column(modifier = Modifier.weight(1f).padding(2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    dayAllDay.forEach { event ->
                        Text(
                            event.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.extraSmall)
                                .clickable { onEventClick(event) }
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            (0..23).forEach { hour ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HOUR_HEIGHT)
                        .border(0.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(
                        modifier = Modifier.width(TIME_COL_WIDTH).padding(top = 2.dp, end = 4.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            "%02d:00".format(hour),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }

                    days.forEach { dayMs ->
                        val hourEvents = timedEvents.filter {
                            isSameDay(it.startTime, dayMs) &&
                                    Calendar.getInstance().apply { timeInMillis = it.startTime }
                                        .get(Calendar.HOUR_OF_DAY) == hour
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(HOUR_HEIGHT)
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(1.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                hourEvents.forEach { event ->
                                    val accentColor = event.tagIds.firstOrNull()
                                        ?.let { tagsById[it] }
                                        ?.let { runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(it.color)) }.getOrNull() }
                                        ?: MaterialTheme.colorScheme.primary
                                    Text(
                                        event.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(accentColor.copy(alpha = 0.85f), MaterialTheme.shapes.extraSmall)
                                            .clickable { onEventClick(event) }
                                            .padding(horizontal = 3.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
