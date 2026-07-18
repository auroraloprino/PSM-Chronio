package com.unibo.android.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.TagModel

internal val PRESET_COLORS = listOf(
    "#E53935", "#D81B60", "#8E24AA", "#3949AB",
    "#1E88E5", "#00ACC1", "#43A047", "#F4511E",
    "#FB8C00", "#F9A825", "#6D4C41", "#546E7A"
)

@Composable
private fun ColorPicker(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PRESET_COLORS.forEach { hex ->
            val color = Color(android.graphics.Color.parseColor(hex))
            val isSelected = hex == selected
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
                    .then(
                        if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                        else Modifier
                    )
                    .clickable { onSelect(hex) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (color.luminance() > 0.4f) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagerSheet(
    visible: Boolean,
    tags: List<TagModel>,
    initialTag: TagModel? = null,
    onSave: (TagModel) -> Unit,
    onUpdate: (TagModel) -> Unit,
    onDelete: (TagModel) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val isEditing = initialTag != null
    var name by remember(initialTag) { mutableStateOf(initialTag?.name ?: "") }
    var color by remember(initialTag) { mutableStateOf(initialTag?.color ?: PRESET_COLORS[0]) }
    var nameError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditing) "Modifica tag" else "Nuovo tag",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = name,
                onValueChange = { if (initialTag?.isSystem != true) { name = it; nameError = false } },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError,
                supportingText = if (nameError) ({ Text("Nome obbligatorio") }) else null,
                shape = RoundedCornerShape(12.dp),
                readOnly = initialTag?.isSystem == true
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Colore", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                ColorPicker(selected = color, onSelect = { color = it })
            }

            Spacer(Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Annulla")
                }
                Button(
                    onClick = {
                        if (name.isBlank()) { nameError = true; return@Button }
                        if (isEditing) onUpdate(initialTag!!.copy(name = name.trim(), color = color))
                        else onSave(TagModel(name = name.trim(), color = color))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isEditing) "Salva" else "Crea")
                }
            }
        }
    }
}
