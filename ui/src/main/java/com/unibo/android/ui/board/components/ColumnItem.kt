package com.unibo.android.ui.board.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.models.ColumnModel
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun ColumnItem(
    column: ColumnModel,
    cards: List<CardModel>,
    onCardClick: (CardModel) -> Unit,
    onToggleCardDone: (CardModel) -> Unit,
    onAddCard: () -> Unit,
    onRenameColumn: () -> Unit,
    onDeleteColumn: () -> Unit,
    modifier: Modifier = Modifier,
    columnDragHandleScope: ReorderableCollectionItemScope? = null,
    draggedCardId: Long? = null,
    dropPreviewIndex: Int? = null,
    onCardBoundsChanged: (cardId: Long, Rect) -> Unit = { _, _ -> },
    onCardDragStart: (card: CardModel, originInRoot: Offset) -> Unit = { _, _ -> },
    onCardDrag: (positionInRoot: Offset) -> Unit = {},
    onCardDragEnd: () -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    val isDropTarget = dropPreviewIndex != null

    Surface(
        modifier = modifier
            .width(280.dp)
            .then(
                if (isDropTarget)
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                else Modifier
            ),
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

            val displayCards = cards.filterNot { it.id == draggedCardId }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(cards, key = { _, c -> c.id }) { _, card ->
                    val isDragged = card.id == draggedCardId
                    val visualIndex = if (isDragged) -1 else displayCards.indexOf(card)

                    if (!isDragged && dropPreviewIndex == visualIndex) {
                        DropPlaceholder()
                    }

                    var cardCoordinates by remember(card.id) {
                        mutableStateOf<LayoutCoordinates?>(null)
                    }

                    CardItem(
                        card = card,
                        onToggleDone = { onToggleCardDone(card) },
                        modifier = Modifier
                            .alpha(if (isDragged) 0f else 1f)
                            .onGloballyPositioned {
                                cardCoordinates = it
                                onCardBoundsChanged(card.id, it.boundsInRoot())
                            }
                            .pointerInput(card) {
                                detectTapGestures(onTap = { onCardClick(card) })
                            }
                            .pointerInput(card) {
                                var current = Offset.Zero
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        current = cardCoordinates?.positionInRoot() ?: Offset.Zero
                                        onCardDragStart(card, current)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        current += dragAmount
                                        onCardDrag(current)
                                    },
                                    onDragEnd = { onCardDragEnd() },
                                    onDragCancel = { onCardDragEnd() }
                                )
                            }
                    )
                }

                if (dropPreviewIndex == displayCards.size) {
                    item { DropPlaceholder() }
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

@Composable
private fun DropPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
    )
}
