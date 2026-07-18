package com.unibo.android.ui.board.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ColumnDialog(
    initialTitle: String = "",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    val isEditing = initialTitle.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Rinomina colonna" else "Nuova colonna") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo") },
                singleLine = true
            )
        },
        confirmButton = {
            DialogActionsRow(
                onDismiss = onDismiss,
                confirmEnabled = title.isNotBlank(),
                confirmLabel = "Salva",
                onConfirm = { onConfirm(title) }
            )
        }
    )
}