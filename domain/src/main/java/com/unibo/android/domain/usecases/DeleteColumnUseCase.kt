package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.ColumnModel
import com.unibo.android.domain.repositories.ColumnRepository

interface DeleteColumnUseCase {
    suspend operator fun invoke(column: ColumnModel): Result<Unit>
}

class DeleteColumnUseCaseImpl(
    private val columnRepository: ColumnRepository
) : DeleteColumnUseCase {
    override suspend operator fun invoke(column: ColumnModel): Result<Unit> =
        try {
            columnRepository.delete(column)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}