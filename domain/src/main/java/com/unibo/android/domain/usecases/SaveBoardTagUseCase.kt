package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.repositories.BoardTagRepository

interface SaveBoardTagUseCase {
    suspend operator fun invoke(tag: BoardTagModel): Result<Long>
}

class SaveBoardTagUseCaseImpl(
    private val boardTagRepository: BoardTagRepository
) : SaveBoardTagUseCase {
    override suspend operator fun invoke(tag: BoardTagModel): Result<Long> {
        if (tag.name.isBlank()) return Result.failure(Exception("Tag name cannot be empty"))
        return try {
            Result.success(boardTagRepository.save(tag))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}