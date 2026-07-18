package com.unibo.android.ui.board.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Riga pulsanti condivisa dai dialog di bacheca/card/tag/colonna: Elimina (se presente) →
 * Annulla → Salva/Crea, in un'unica riga in modo che l'AlertDialog li allinei tutti insieme.
 */
@Composable
fun DialogActionsRow(
    onDismiss: () -> Unit,
    confirmEnabled: Boolean,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Row {
        if (onDelete != null) {
            TextButton(onClick = onDelete) {
                Text("Elimina", color = MaterialTheme.colorScheme.error)
            }
        }
        TextButton(onClick = onDismiss) { Text("Annulla") }
        TextButton(onClick = onConfirm, enabled = confirmEnabled) { Text(confirmLabel) }
    }
}
