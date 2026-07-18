package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardModel
import com.unibo.android.domain.repositories.BoardRepository

interface DeleteBoardUseCase {
    suspend operator fun invoke(board: BoardModel)
}

class DeleteBoardUseCaseImpl(
    private val boardRepository: BoardRepository
) : DeleteBoardUseCase {
    override suspend fun invoke(board: BoardModel) {
        boardRepository.delete(board)
    }
}
