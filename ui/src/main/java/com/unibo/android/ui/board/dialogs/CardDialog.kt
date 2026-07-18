package com.unibo.android.ui.board.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.models.CardModel
import com.unibo.android.ui.board.components.toColorOrDefault

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CardDialog(
    card: CardModel?,
    availableTags: List<BoardTagModel>,
    onConfirm: (title: String, description: String, tagIds: List<Long>) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val isEditing = card != null

    var title by remember(card?.id) { mutableStateOf(card?.title ?: "") }
    var description by remember(card?.id) { mutableStateOf(card?.description ?: "") }

    val selectedTagIds = remember(card?.id) {
        (card?.tags?.map { it.id } ?: emptyList()).toMutableStateList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Modifica card" else "Nuova card") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titolo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                if (availableTags.isNotEmpty()) {
                    Text("Tag", style = MaterialTheme.typography.labelMedium)

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableTags.forEach { tag ->
                            val selected = tag.id in selectedTagIds
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    if (selected) selectedTagIds.remove(tag.id)
                                    else selectedTagIds.add(tag.id)
                                },
                                label = { Text(tag.name) },
                                leadingIcon = {
                                    Surface(
                                        color = tag.color.toColorOrDefault(),
                                        shape = CircleShape,
                                        modifier = Modifier.size(12.dp)
                                    ) {}
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor =
                                        tag.color.toColorOrDefault().copy(alpha = 0.25f)
                                )
                            )
                        }
                    }
                } else {
                    Text(
                        "Nessun tag disponibile. Creane uno dalla barra dei filtri.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            DialogActionsRow(
                onDelete = onDelete.takeIf { isEditing },
                onDismiss = onDismiss,
                confirmEnabled = title.isNotBlank(),
                confirmLabel = "Salva",
                onConfirm = { onConfirm(title, description, selectedTagIds.toList()) }
            )
        }
    )
}
