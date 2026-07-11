package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.ColumnModel
import com.unibo.android.domain.repositories.ColumnRepository

interface ReorderColumnsUseCase {
    suspend operator fun invoke(orderedColumns: List<ColumnModel>): Result<Unit>
}

class ReorderColumnsUseCaseImpl(
    private val columnRepository: ColumnRepository
) : ReorderColumnsUseCase {
    override suspend operator fun invoke(orderedColumns: List<ColumnModel>): Result<Unit> =
        try {
            val renumbered = orderedColumns.mapIndexed { index, c -> c.copy(position = index) }
            columnRepository.updateAll(renumbered)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}