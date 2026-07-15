package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.repositories.BoardTagRepository

interface DeleteBoardTagUseCase {
    suspend operator fun invoke(tag: BoardTagModel): Result<Unit>
}

class DeleteBoardTagUseCaseImpl(
    private val boardTagRepository: BoardTagRepository
) : DeleteBoardTagUseCase {

    override suspend operator fun invoke(tag: BoardTagModel): Result<Unit> =
        try {
            boardTagRepository.delete(tag)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}