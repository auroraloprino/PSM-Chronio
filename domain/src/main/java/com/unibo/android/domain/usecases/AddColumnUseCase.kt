package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.ColumnModel
import com.unibo.android.domain.repositories.ColumnRepository

interface AddColumnUseCase {
    suspend operator fun invoke(boardId: Long, title: String): Result<Long>
}

class AddColumnUseCaseImpl(
    private val columnRepository: ColumnRepository
) : AddColumnUseCase {
    override suspend operator fun invoke(boardId: Long, title: String): Result<Long> {
        if (title.isBlank()) return Result.failure(Exception("Column title cannot be empty"))
        return try {
            val position = columnRepository.nextPosition(boardId)
            val id = columnRepository.save(
                ColumnModel(title = title, position = position, boardId = boardId)
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}