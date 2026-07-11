package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.ColumnEntity
import com.unibo.android.domain.models.ColumnModel
import com.unibo.android.domain.repositories.ColumnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ColumnRepositoryImpl(context: Context) : ColumnRepository {
    private val columnDao = ChronioDatabase.getInstance(context).columnDao()

    override fun observeColumns(boardId: Long): Flow<List<ColumnModel>> =
        columnDao.observeForBoard(boardId).map { list -> list.map { it.toModel() } }

    override suspend fun nextPosition(boardId: Long): Int =
        columnDao.nextPosition(boardId)

    override suspend fun save(column: ColumnModel): Long =
        columnDao.insert(column.toEntity())

    override suspend fun update(column: ColumnModel) =
        columnDao.update(column.toEntity())

    override suspend fun updateAll(columns: List<ColumnModel>) =
        columnDao.updateAll(columns.map { it.toEntity() })

    override suspend fun delete(column: ColumnModel) =
        columnDao.delete(column.toEntity())

    private fun ColumnEntity.toModel() = ColumnModel(id, title, position, boardId)
    private fun ColumnModel.toEntity() = ColumnEntity(id, title, position, boardId)
}