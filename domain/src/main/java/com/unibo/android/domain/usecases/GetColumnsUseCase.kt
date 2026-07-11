package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.ColumnModel
import com.unibo.android.domain.repositories.ColumnRepository
import kotlinx.coroutines.flow.Flow

interface GetColumnsUseCase {
    operator fun invoke(boardId: Long): Flow<List<ColumnModel>>
}

class GetColumnsUseCaseImpl(
    private val columnRepository: ColumnRepository
) : GetColumnsUseCase {
    override fun invoke(boardId: Long): Flow<List<ColumnModel>> =
        columnRepository.observeColumns(boardId)
}
