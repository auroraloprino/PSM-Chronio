package com.unibo.android.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.TagModel

private val PRESET_COLORS = listOf(
    "#E53935", "#D81B60", "#8E24AA", "#3949AB",
    "#1E88E5", "#00ACC1", "#43A047", "#F4511E",
    "#FB8C00", "#F9A825", "#6D4C41", "#546E7A"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagerSheet(
    tags: List<TagModel>,
    onSave: (TagModel) -> Unit,
    onDelete: (TagModel) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(PRESET_COLORS[0]) }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Gestisci tag", style = MaterialTheme.typography.titleMedium)

            if (tags.isNotEmpty()) {
                tags.forEach { tag ->
                    val color = runCatching { Color(android.graphics.Color.parseColor(tag.color)) }
                        .getOrDefault(Color(0xFF6200EE))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Text(tag.name, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { onDelete(tag) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina tag", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Text("Nuovo tag", style = MaterialTheme.typography.labelMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                supportingText = error?.let { { Text(it) } }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PRESET_COLORS.forEach { hex ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (hex == selectedColor)
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = hex }
                    )
                }
            }

            Button(
                onClick = {
                    if (name.isBlank()) { error = "Nome obbligatorio"; return@Button }
                    onSave(TagModel(name = name.trim(), color = selectedColor))
                    name = ""
                    selectedColor = PRESET_COLORS[0]
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Crea tag")
            }
        }
    }
}
