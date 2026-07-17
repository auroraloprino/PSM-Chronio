package com.unibo.android.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.TagChip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private fun formatDateTime(ms: Long): String =
    SimpleDateFormat("EEE d MMM, HH:mm", Locale.getDefault()).format(ms)

private fun formatDateOnly(ms: Long): String =
    SimpleDateFormat("EEE d MMM yyyy", Locale.getDefault()).format(ms)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventForm(
    initial: EventModel? = null,
    tags: List<TagModel>,
    selectedDay: Long,
    presetStartTime: Long? = null,
    onSave: (EventModel, List<Long>) -> Unit,
    onDelete: ((EventModel) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var startTime by remember { mutableLongStateOf(initial?.startTime ?: (presetStartTime ?: selectedDay)) }
    var endTime by remember { mutableLongStateOf(initial?.endTime ?: ((presetStartTime ?: selectedDay) + 3600_000L)) }
    var reminder by remember { mutableStateOf((initial?.reminderMinutes ?: 30).toString()) }
    var selectedTagIds by remember { mutableStateOf(initial?.tagIds?.toSet() ?: emptySet()) }
    var endTimeEdited by remember { mutableStateOf(initial != null) }
    var allDay by remember {
        mutableStateOf(
            if (initial != null) (initial.endTime - initial.startTime) >= 24 * 3600_000L
            else false
        )
    }
    var showTagSheet by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun autoEnd(newStart: Long) {
        if (!endTimeEdited) endTime = newStart + 3600_000L
    }

    fun pickDate(current: Long, onPicked: (Long) -> Unit) {
        val cal = Calendar.getInstance().apply { timeInMillis = current }
        DatePickerDialog(context, { _, y, m, d ->
            val result = Calendar.getInstance().apply {
                timeInMillis = current
                set(Calendar.YEAR, y); set(Calendar.MONTH, m); set(Calendar.DAY_OF_MONTH, d)
            }.timeInMillis
            onPicked(result)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun pickTime(current: Long, onPicked: (Long) -> Unit) {
        val cal = Calendar.getInstance().apply { timeInMillis = current }
        TimePickerDialog(context, { _, h, min ->
            val result = Calendar.getInstance().apply {
                timeInMillis = current
                set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, min)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            onPicked(result)
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    if (showTagSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTagSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp)
            ) {
                Text(
                    "Seleziona tag",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (tags.isEmpty()) {
                    Text(
                        "Nessun tag",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    tags.forEach { tag ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTagIds = selectedTagIds.toMutableSet().also {
                                        if (tag.id in it) it.remove(tag.id) else it.add(tag.id)
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TagChip(tag = tag, selected = tag.id in selectedTagIds)
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (initial == null) "Nuovo evento" else "Modifica evento",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it; error = null },
            label = { Text("Titolo") },
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrizione") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tutto il giorno", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = allDay, onCheckedChange = { allDay = it })
        }

        if (allDay) {
            OutlinedButton(
                onClick = { pickDate(startTime) { startTime = it; endTime = it + 24 * 3600_000L } },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Data: ${formatDateOnly(startTime)}") }
        } else {
            OutlinedButton(
                onClick = { pickDate(startTime) { d -> pickTime(d) { startTime = it; autoEnd(it) } } },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Inizio: ${formatDateTime(startTime)}") }
            OutlinedButton(
                onClick = { pickDate(endTime) { d -> pickTime(d) { endTime = it; endTimeEdited = true } } },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Fine: ${formatDateTime(endTime)}") }
        }

        OutlinedTextField(
            value = reminder,
            onValueChange = { reminder = it },
            label = { Text("Reminder (minuti prima)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        val tagLabel = if (selectedTagIds.isEmpty()) "Nessun tag"
        else tags.filter { it.id in selectedTagIds }.joinToString(", ") { it.name }
        OutlinedButton(
            onClick = { showTagSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Tag: $tagLabel") }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancella") }
            Button(
                onClick = {
                    if (title.isBlank()) { error = "Titolo mancante"; return@Button }
                    if (!allDay && endTime <= startTime) { error = "Fine deve essere dopo inizio"; return@Button }
                    val event = EventModel(
                        id = initial?.id ?: 0,
                        title = title.trim(),
                        description = description.trim(),
                        startTime = startTime,
                        endTime = if (allDay) startTime + 24 * 3600_000L else endTime,
                        reminderMinutes = reminder.toIntOrNull() ?: 30
                    )
                    onSave(event, selectedTagIds.toList())
                },
                modifier = Modifier.weight(1f)
            ) { Text("Salva") }
        }

        if (initial != null && onDelete != null) {
            Button(
                onClick = { onDelete(initial) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Elimina") }
        }
    }
}
