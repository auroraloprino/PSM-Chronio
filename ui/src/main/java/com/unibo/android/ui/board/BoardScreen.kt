package com.unibo.android.ui.board

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

internal data class CardDragState(
    val card: CardModel,
    val sourceColumnId: Long,
    val topLeftInRoot: Offset
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
    val draggingCards by vm.draggingCards.collectAsState()
    val draggingColumns by vm.draggingColumns.collectAsState()

    var dialog by remember { mutableStateOf<DialogState>(DialogState.None) }
    var cardDragState by remember { mutableStateOf<CardDragState?>(null) }
    val columnBounds = remember { mutableStateMapOf<Long, Rect>() }

    val displayColumns = draggingColumns
        ?: state.columns.map { it.column }

    val lazyRowState = rememberLazyListState()
    val reorderableRowState = rememberReorderableLazyListState(lazyRowState) { from, to ->
        vm.onColumnMove(from.index, to.index)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

                            val cards = draggingCards?.get(column.id)
                                ?: state.columns.find { it.column.id == column.id }?.cards
                                ?: emptyList()

                            ColumnItem(
                                column = column,
                                cards = cards,
                                columnDragHandleScope = this,  // scope per l'handle colonna
                                onCardMove = { from, to -> vm.onCardMove(column.id, from, to) },
                                onCardDragStopped = { vm.onCardDragStopped(column.id) },
                                onCardClick = { dialog = DialogState.EditCard(it) },
                                onAddCard = { dialog = DialogState.NewCard(column.id) },
                                onRenameColumn = { dialog = DialogState.RenameColumn(column) },
                                onDeleteColumn = { vm.deleteColumn(column) },
                                isDropTarget = cardDragState != null &&
                                    cardDragState?.sourceColumnId != column.id &&
                                    columnBounds[column.id]?.contains(cardDragState!!.topLeftInRoot) == true,
                                onColumnBoundsChanged = { rect -> columnBounds[column.id] = rect },
                                onCardCrossColumnDrag = { card, rootPosition ->
                                    cardDragState = if (rootPosition == null) {
                                        val dropTargetId = columnBounds.entries.firstOrNull { (id, rect) ->
                                            id != column.id && cardDragState != null &&
                                                rect.contains(cardDragState!!.topLeftInRoot)
                                        }?.key
                                        if (dropTargetId != null) {
                                            vm.moveCardToColumn(card, dropTargetId)
                                        }
                                        null
                                    } else {
                                        CardDragState(card, column.id, rootPosition)
                                    }
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
