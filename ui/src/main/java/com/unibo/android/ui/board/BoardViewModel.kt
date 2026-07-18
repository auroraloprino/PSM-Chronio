package com.unibo.android.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.android.domain.di.UseCasesProvider
import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.models.ColumnModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterMode { OR, AND }

data class ColumnUi(
    val column: ColumnModel,
    val cards: List<CardModel>
)

data class BoardUiState(
    val columns: List<ColumnUi> = emptyList(),
    val tags: List<BoardTagModel> = emptyList(),
    val activeFilters: Set<Long> = emptySet(),
    val filterMode: FilterMode = FilterMode.OR,
    val isFiltering: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModel(
    private val boardId: Long
) : ViewModel() {

    private val activeFilters = MutableStateFlow<Set<Long>>(emptySet())
    private val filterMode = MutableStateFlow(FilterMode.OR)

    private val _draggingColumns = MutableStateFlow<List<ColumnModel>?>(null)
    val draggingColumns: StateFlow<List<ColumnModel>?> = _draggingColumns

    private val columnsFlow = UseCasesProvider.getColumnsUseCase(boardId)

    private val columnsWithCardsFlow = columnsFlow.flatMapLatest { columns ->
        if (columns.isEmpty()) {
            MutableStateFlow(emptyList<ColumnUi>())
        } else {
            val perColumnCardFlows = columns.map { column ->
                UseCasesProvider.getCardsUseCase(column.id)
            }
            combine(perColumnCardFlows) { cardLists ->
                columns.mapIndexed { index, column ->
                    ColumnUi(column = column, cards = cardLists[index])
                }
            }
        }
    }

    val uiState: StateFlow<BoardUiState> =
        combine(
            columnsWithCardsFlow,
            UseCasesProvider.getBoardTagsUseCase(),
            activeFilters,
            filterMode
        ) { columnsWithCards, tags, filters, mode ->
            val filtered = if (filters.isEmpty()) {
                columnsWithCards
            } else {
                columnsWithCards.map { colUi ->
                    colUi.copy(cards = colUi.cards.filter { card ->
                        val cardTagIds = card.tags.map { it.id }.toSet()
                        when (mode) {
                            FilterMode.OR -> cardTagIds.any { it in filters }
                            FilterMode.AND -> filters.all { it in cardTagIds }
                        }
                    })
                }
            }
            BoardUiState(
                columns = filtered,
                tags = tags,
                activeFilters = filters,
                filterMode = mode,
                isFiltering = filters.isNotEmpty()
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BoardUiState())

    /**
     * Moves [card] so that it lands at [targetIndex] within [targetColumnId]'s card list
     * (same column for a local reorder, different column for a cross-column move).
     */
    fun moveCard(card: CardModel, targetColumnId: Long, targetIndex: Int) = viewModelScope.launch {
        val targetCards = uiState.value.columns.find { it.column.id == targetColumnId }?.cards
            ?.filterNot { it.id == card.id }
            ?: emptyList()
        val reordered = targetCards.toMutableList().apply {
            add(targetIndex.coerceIn(0, size), card)
        }
        if (card.columnId == targetColumnId) {
            UseCasesProvider.moveCardUseCase.reorderWithinColumn(reordered)
        } else {
            UseCasesProvider.moveCardUseCase.moveToColumnAtPosition(card, targetColumnId, reordered)
        }
    }

    fun onColumnMove(fromIndex: Int, toIndex: Int) {
        val current = _draggingColumns.value
            ?: uiState.value.columns.map { it.column }
        val mutated = current.toMutableList().apply {
            if (fromIndex in indices && toIndex in 0..size) {
                add(toIndex, removeAt(fromIndex))
            }
        }
        _draggingColumns.value = mutated
    }

    fun onColumnDragStopped() {
        val finalOrder = _draggingColumns.value ?: return
        viewModelScope.launch {
            UseCasesProvider.reorderColumnsUseCase(finalOrder)
            _draggingColumns.value = null
        }
    }

    fun toggleFilter(tagId: Long) {
        val cur = activeFilters.value.toMutableSet()
        if (!cur.add(tagId)) cur.remove(tagId)
        activeFilters.value = cur
    }

    fun clearFilters() { activeFilters.value = emptySet() }
    fun setFilterMode(mode: FilterMode) { filterMode.value = mode }

    fun addColumn(title: String) = viewModelScope.launch {
        UseCasesProvider.addColumnUseCase(boardId, title)
    }
    fun renameColumn(column: ColumnModel, newTitle: String) = viewModelScope.launch {
        UseCasesProvider.reorderColumnsUseCase(listOf(column.copy(title = newTitle)))
    }
    fun deleteColumn(column: ColumnModel) = viewModelScope.launch {
        UseCasesProvider.deleteColumnUseCase(column)
    }

    fun saveCard(card: CardModel, tagIds: List<Long>) = viewModelScope.launch {
        UseCasesProvider.saveCardUseCase(card, tagIds)
    }
    fun deleteCard(card: CardModel) = viewModelScope.launch {
        UseCasesProvider.deleteCardUseCase(card)
    }

    fun createTag(name: String, color: String) = viewModelScope.launch {
        UseCasesProvider.saveBoardTagUseCase(BoardTagModel(name = name, color = color))
    }
    fun updateTag(tag: BoardTagModel) = viewModelScope.launch {
        UseCasesProvider.updateBoardTagUseCase(tag)
    }
    fun deleteTag(tag: BoardTagModel) = viewModelScope.launch {
        UseCasesProvider.deleteBoardTagUseCase(tag)
        activeFilters.value = activeFilters.value - tag.id
    }
}