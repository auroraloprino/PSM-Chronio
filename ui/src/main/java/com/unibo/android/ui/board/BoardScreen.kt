package com.unibo.android.ui.board

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.models.ColumnModel
import com.unibo.android.ui.board.components.CardItem
import com.unibo.android.ui.board.components.ColumnItem
import com.unibo.android.ui.board.components.TagFilterRow
import com.unibo.android.ui.board.dialogs.CardDialog
import com.unibo.android.ui.board.dialogs.ColumnDialog
import com.unibo.android.ui.board.dialogs.TagDialog
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * Live state of a card being dragged. [targetColumnId]/[targetIndex] sono ricalcolati ad ogni
 * movimento: la colonna cambia appena il dito esce (orizzontalmente) dalla colonna di origine,
 * l'indice viene scelto confrontando la posizione verticale con le card già presenti.
 */
private data class CardDragState(
    val card: CardModel,
    val sourceColumnId: Long,
    val topLeftInRoot: Offset,
    val targetColumnId: Long,
    val targetIndex: Int
)

private sealed interface DialogState {
    data object None : DialogState
    data object NewColumn : DialogState
    data class RenameColumn(val column: ColumnModel) : DialogState
    data class NewCard(val columnId: Long) : DialogState
    data class EditCard(val card: CardModel) : DialogState
    data object NewTag : DialogState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    boardId: Long,
    boardTitle: String,
    onBack: () -> Unit,
    vm: BoardViewModel = viewModel { BoardViewModel(boardId) }
) {
    val state by vm.uiState.collectAsState()
    val draggingColumns by vm.draggingColumns.collectAsState()

    var dialog by remember { mutableStateOf<DialogState>(DialogState.None) }
    var cardDragState by remember { mutableStateOf<CardDragState?>(null) }
    val cardBounds = remember { mutableStateMapOf<Long, Rect>() }

    val displayColumns = draggingColumns
        ?: state.columns.map { it.column }

    val lazyRowState = rememberLazyListState()
    val reorderableRowState = rememberReorderableLazyListState(lazyRowState) { from, to ->
        vm.onColumnMove(from.index, to.index)
    }

    val density = LocalDensity.current
    // Zona vicino al bordo dello SCHERMO (non della colonna, che potrebbe essere in parte fuori
    // vista e quindi irraggiungibile col dito): basta entrarci per puntare al vicino e avviare
    // lo scroll automatico che lo porta in vista.
    val edgeZonePx = with(density) { 56.dp.toPx() }

    fun neighborColumnId(sourceColumnId: Long, direction: Int): Long? {
        val idx = displayColumns.indexOfFirst { it.id == sourceColumnId }
        if (idx == -1) return null
        return displayColumns.getOrNull(idx + direction)?.id
    }

    fun indexInColumn(columnId: Long, pointerY: Float, draggedCardId: Long): Int {
        val columnCards = (state.columns.find { it.column.id == columnId }?.cards ?: emptyList())
            .filterNot { it.id == draggedCardId }
        return columnCards.indexOfFirst { c ->
            val rect = cardBounds[c.id]
            rect != null && pointerY < (rect.top + rect.bottom) / 2f
        }.let { if (it == -1) columnCards.size else it }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()

        fun targetColumnFor(sourceColumnId: Long, position: Offset): Long {
            val result = when {
                position.x < edgeZonePx -> neighborColumnId(sourceColumnId, -1) ?: sourceColumnId
                position.x > screenWidthPx - edgeZonePx -> neighborColumnId(sourceColumnId, 1) ?: sourceColumnId
                else -> sourceColumnId
            }
            android.util.Log.d(
                "DragDebug",
                "x=${position.x} screenW=$screenWidthPx edge=$edgeZonePx source=$sourceColumnId -> target=$result"
            )
            return result
        }

        LaunchedEffect(cardDragState != null) {
            while (cardDragState != null) {
                val x = cardDragState?.topLeftInRoot?.x
                when {
                    x != null && x < edgeZonePx -> lazyRowState.scrollBy(-24f)
                    x != null && x > screenWidthPx - edgeZonePx -> lazyRowState.scrollBy(24f)
                }
                delay(16)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(boardTitle) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { dialog = DialogState.NewColumn },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Colonna") }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                TagFilterRow(
                    tags = state.tags,
                    activeFilters = state.activeFilters,
                    onToggleFilter = vm::toggleFilter,
                    onClearFilters = vm::clearFilters,
                    onCreateTag = { dialog = DialogState.NewTag }
                )

                if (displayColumns.isEmpty()) {
                    EmptyBoardMessage(isFiltering = state.isFiltering)
                } else {
                    LazyRow(
                        state = lazyRowState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayColumns, key = { it.id }) { column ->
                            ReorderableItem(reorderableRowState, key = column.id) { isDragging ->
                                val elevation by animateDpAsState(
                                    targetValue = if (isDragging) 8.dp else 0.dp,
                                    label = "columnElevation"
                                )

                                val cards = state.columns.find { it.column.id == column.id }?.cards
                                    ?: emptyList()

                                val dragState = cardDragState

                                ColumnItem(
                                    column = column,
                                    cards = cards,
                                    columnDragHandleScope = this,  // scope per l'handle colonna
                                    onCardClick = { dialog = DialogState.EditCard(it) },
                                    onAddCard = { dialog = DialogState.NewCard(column.id) },
                                    onRenameColumn = { dialog = DialogState.RenameColumn(column) },
                                    onDeleteColumn = { vm.deleteColumn(column) },
                                    draggedCardId = dragState?.card?.id,
                                    dropPreviewIndex = if (dragState?.targetColumnId == column.id) dragState.targetIndex else null,
                                    onCardBoundsChanged = { cardId, rect -> cardBounds[cardId] = rect },
                                    onCardDragStart = { card, originInRoot ->
                                        val targetColumnId = targetColumnFor(column.id, originInRoot)
                                        val index = indexInColumn(targetColumnId, originInRoot.y, card.id)
                                        cardDragState = CardDragState(card, column.id, originInRoot, targetColumnId, index)
                                    },
                                    onCardDrag = { positionInRoot ->
                                        cardDragState?.let { current ->
                                            val targetColumnId = targetColumnFor(current.sourceColumnId, positionInRoot)
                                            val index = indexInColumn(targetColumnId, positionInRoot.y, current.card.id)
                                            cardDragState = current.copy(
                                                topLeftInRoot = positionInRoot,
                                                targetColumnId = targetColumnId,
                                                targetIndex = index
                                            )
                                        }
                                    },
                                    onCardDragEnd = {
                                        cardDragState?.let { final ->
                                            vm.moveCard(final.card, final.targetColumnId, final.targetIndex)
                                        }
                                        cardDragState = null
                                    },
                                    modifier = Modifier.fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            }
        }

        cardDragState?.let { dragState ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(dragState.topLeftInRoot.x.roundToInt(), dragState.topLeftInRoot.y.roundToInt())
                    }
                    .width(260.dp)
                    .graphicsLayer { alpha = 0.85f; shadowElevation = 16f }
            ) {
                CardItem(card = dragState.card, onClick = {})
            }
        }
    }

    when (val d = dialog) {
        is DialogState.None -> Unit

        is DialogState.NewColumn -> ColumnDialog(
            onConfirm = { title -> vm.addColumn(title); dialog = DialogState.None },
            onDismiss = { dialog = DialogState.None }
        )

        is DialogState.RenameColumn -> ColumnDialog(
            initialTitle = d.column.title,
            onConfirm = { title -> vm.renameColumn(d.column, title); dialog = DialogState.None },
            onDismiss = { dialog = DialogState.None }
        )

        is DialogState.NewCard -> CardDialog(
            card = null,
            availableTags = state.tags,
            onConfirm = { title, desc, tagIds ->
                vm.saveCard(
                    CardModel(title = title, description = desc, columnId = d.columnId, position = 0),
                    tagIds
                )
                dialog = DialogState.None
            },
            onDismiss = { dialog = DialogState.None }
        )

        is DialogState.EditCard -> CardDialog(
            card = d.card,
            availableTags = state.tags,
            onConfirm = { title, desc, tagIds ->
                vm.saveCard(d.card.copy(title = title, description = desc), tagIds)
                dialog = DialogState.None
            },
            onDelete = { vm.deleteCard(d.card); dialog = DialogState.None },
            onDismiss = { dialog = DialogState.None }
        )

        is DialogState.NewTag -> TagDialog(
            onConfirm = { name, color -> vm.createTag(name, color); dialog = DialogState.None },
            onDismiss = { dialog = DialogState.None }
        )
    }
}

@Composable
private fun EmptyBoardMessage(isFiltering: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isFiltering)
                "Nessuna card corrisponde ai filtri attivi"
            else
                "Nessuna colonna. Creane una con il pulsante in basso.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp)
        )
    }
}
