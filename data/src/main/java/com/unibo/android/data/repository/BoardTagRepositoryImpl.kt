package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.BoardTagEntity
import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.repositories.BoardTagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BoardTagRepositoryImpl(context: Context) : BoardTagRepository {
    private val boardTagDao = ChronioDatabase.getInstance(context).boardTagDao()

    override fun observeAll(): Flow<List<BoardTagModel>> =
        boardTagDao.observeAll().map { list -> list.map { it.toModel() } }

    override suspend fun save(tag: BoardTagModel): Long =
        boardTagDao.insert(tag.toEntity())

    override suspend fun update(tag: BoardTagModel) =
        boardTagDao.update(tag.toEntity())

    override suspend fun delete(tag: BoardTagModel) =
        boardTagDao.delete(tag.toEntity())

    private fun BoardTagEntity.toModel() = BoardTagModel(id, name, color)
    private fun BoardTagModel.toEntity() = BoardTagEntity(id, name, color)
}