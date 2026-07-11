package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.ColumnModel
import kotlinx.coroutines.flow.Flow

interface ColumnRepository {
    fun observeColumns(boardId: Long): Flow<List<ColumnModel>>
    suspend fun nextPosition(boardId: Long): Int
    suspend fun save(column: ColumnModel): Long
    suspend fun update(column: ColumnModel)
    suspend fun updateAll(columns: List<ColumnModel>)
    suspend fun delete(column: ColumnModel)
}