package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.TagModel
import com.unibo.android.domain.repositories.TagRepository

interface SaveTagUseCase {
    suspend operator fun invoke(tag: TagModel): Result<Unit>
}

class SaveTagUseCaseImpl(
    private val tagRepository: TagRepository
) : SaveTagUseCase {
    override suspend operator fun invoke(tag: TagModel): Result<Unit> {
        if (tag.name.isBlank()) return Result.failure(Exception("Tag name cannot be empty"))
        return try {
            tagRepository.save(tag)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
