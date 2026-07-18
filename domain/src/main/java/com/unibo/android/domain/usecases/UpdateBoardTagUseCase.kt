package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.repositories.BoardTagRepository

interface UpdateBoardTagUseCase {
    suspend operator fun invoke(tag: BoardTagModel): Result<Unit>
}

class UpdateBoardTagUseCaseImpl(
    private val boardTagRepository: BoardTagRepository
) : UpdateBoardTagUseCase {
    override suspend operator fun invoke(tag: BoardTagModel): Result<Unit> {
        if (tag.name.isBlank()) return Result.failure(Exception("Tag name cannot be empty"))
        return try {
            boardTagRepository.update(tag)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
