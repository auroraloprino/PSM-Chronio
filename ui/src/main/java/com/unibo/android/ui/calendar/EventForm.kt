package com.unibo.android.ui.calendar

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.models.TagModel
import com.unibo.android.ui.utils.TagChip

@Composable
fun EventForm(
    initial: EventModel? = null,
    tags: List<TagModel>,
    selectedDay: Long,
    onSave: (EventModel, List<Long>) -> Unit,
    onDelete: ((EventModel) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var startTime by remember { mutableLongStateOf(initial?.startTime ?: selectedDay) }
    var endTime by remember { mutableLongStateOf(initial?.endTime ?: (selectedDay + 3600_000L)) }
    var reminder by remember { mutableStateOf((initial?.reminderMinutes ?: 30).toString()) }
    var selectedTagIds by remember { mutableStateOf(emptySet<Long>()) }
    var error by remember { mutableStateOf<String?>(null) }

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

        OutlinedTextField(
            value = reminder,
            onValueChange = { reminder = it },
            label = { Text("Reminder (minuti prima)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (tags.isNotEmpty()) {
            Text("Tags", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    TagChip(
                        tag = tag,
                        selected = tag.id in selectedTagIds,
                        onClick = {
                            selectedTagIds = selectedTagIds.toMutableSet().also {
                                if (tag.id in it) it.remove(tag.id) else it.add(tag.id)
                            }
                        }
                    )
                }
            }
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancella") }

            Button(
                onClick = {
                    if (title.isBlank()) { error = "Titolo Mancante"; return@Button }
                    val event = EventModel(
                        id = initial?.id ?: 0,
                        title = title.trim(),
                        description = description.trim(),
                        startTime = startTime,
                        endTime = endTime,
                        reminderMinutes = reminder.toIntOrNull() ?: 30
                    )
                    onSave(event, selectedTagIds.toList())
                },
                modifier = Modifier.weight(1f)
            ) { Text("Save") }
        }

        if (initial != null && onDelete != null) {
            Button(
                onClick = { onDelete(initial) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Delete") }
        }
    }
}
