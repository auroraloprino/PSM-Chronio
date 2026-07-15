package com.unibo.android.ui.board.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.models.ColumnModel
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ColumnItem(
    column: ColumnModel,
    cards: List<CardModel>,
    onCardMove: (from: Int, to: Int) -> Unit,
    onCardDragStopped: () -> Unit,
    onCardClick: (CardModel) -> Unit,
    onAddCard: () -> Unit,
    onRenameColumn: () -> Unit,
    onDeleteColumn: () -> Unit,
    modifier: Modifier = Modifier,
    columnDragHandleScope: ReorderableCollectionItemScope? = null
) {
    val lazyListState = rememberLazyListState()

    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onCardMove(from.index, to.index)
    }

    Surface(
        modifier = modifier.width(280.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (columnDragHandleScope != null) {
                    Icon(
                        imageVector = Icons.Rounded.DragHandle,
                        contentDescription = "Trascina colonna",
                        modifier = with(columnDragHandleScope) {
                            Modifier.size(20.dp).draggableHandle()
                        },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = column.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )

                Text(
                    text = "${cards.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                var menuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opzioni colonna")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rinomina") },
                            onClick = { menuExpanded = false; onRenameColumn() }
                        )
                        DropdownMenuItem(
                            text = { Text("Elimina") },
                            onClick = { menuExpanded = false; onDeleteColumn() }
                        )
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    ReorderableItem(reorderableState, key = card.id) { isDragging ->
                        val elevation by animateDpAsState(
                            targetValue = if (isDragging) 8.dp else 1.dp,
                            label = "cardElevation"
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CardItem(
                                card = card,
                                onClick = { onCardClick(card) },
                                elevation = elevation,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {},
                                modifier = Modifier.draggableHandle(
                                    onDragStopped = { onCardDragStopped() }
                                )
                            ) {
                                Icon(
                                    Icons.Rounded.DragHandle,
                                    contentDescription = "Trascina card",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    androidx.compose.material3.TextButton(
                        onClick = onAddCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Aggiungi card", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }
}