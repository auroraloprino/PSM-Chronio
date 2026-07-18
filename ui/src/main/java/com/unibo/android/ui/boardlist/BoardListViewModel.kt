package com.unibo.android.ui.boardlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.android.domain.di.UseCasesProvider
import com.unibo.android.domain.models.BoardModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BoardListViewModel : ViewModel() {

    val boards: StateFlow<List<BoardModel>> =
        UseCasesProvider.getBoardsUseCase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun createBoard(title: String, description: String, coverImageUrl: String?) =
        viewModelScope.launch {
            UseCasesProvider.saveBoardUseCase(
                BoardModel(
                    title = title,
                    description = description,
                    coverImageUrl = coverImageUrl
                )
            )
        }

    fun updateBoard(board: BoardModel, title: String, description: String, coverImageUrl: String?) =
        viewModelScope.launch {
            UseCasesProvider.saveBoardUseCase(
                board.copy(
                    title = title,
                    description = description,
                    coverImageUrl = coverImageUrl
                )
            )
        }

    fun deleteBoard(board: BoardModel) = viewModelScope.launch {
        UseCasesProvider.deleteBoardUseCase(board)
    }
}