package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.BoardEntity
import com.unibo.android.domain.models.BoardModel
import com.unibo.android.domain.repositories.BoardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BoardRepositoryImpl(context: Context) : BoardRepository {
    private val boardDao = ChronioDatabase.getInstance(context).boardDao()

    override fun observeBoards(): Flow<List<BoardModel>> =
        boardDao.observeAll().map { list -> list.map { it.toModel() } }

    override suspend fun getById(id: Long): BoardModel? =
        boardDao.getById(id)?.toModel()

    override suspend fun save(board: BoardModel): Long =
        boardDao.insert(board.toEntity())

    override suspend fun update(board: BoardModel) =
        boardDao.update(board.toEntity())

    override suspend fun delete(board: BoardModel) =
        boardDao.delete(board.toEntity())

    private fun BoardEntity.toModel() =
        BoardModel(id, title, description, coverImageUrl, createdAt)

    private fun BoardModel.toEntity() =
        BoardEntity(id, title, description, coverImageUrl, createdAt)
}