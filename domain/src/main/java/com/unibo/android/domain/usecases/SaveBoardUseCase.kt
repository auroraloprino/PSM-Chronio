package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardModel
import com.unibo.android.domain.repositories.BoardRepository

interface SaveBoardUseCase {
    suspend operator fun invoke(board: BoardModel): Result<Long>
}

class SaveBoardUseCaseImpl(
    private val boardRepository: BoardRepository
) : SaveBoardUseCase {
    override suspend operator fun invoke(board: BoardModel): Result<Long> {
        if (board.title.isBlank()) return Result.failure(Exception("Title cannot be empty"))
        return try {
            Result.success(boardRepository.save(board))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}