package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardModel
import com.unibo.android.domain.repositories.BoardRepository
import kotlinx.coroutines.flow.Flow

interface GetBoardsUseCase {
    operator fun invoke(): Flow<List<BoardModel>>
}

class GetBoardsUseCaseImpl(
    private val boardRepository: BoardRepository
) : GetBoardsUseCase {
    override fun invoke(): Flow<List<BoardModel>> = boardRepository.observeBoards()
}