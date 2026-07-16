package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.BoardEntity
import com.unibo.android.domain.models.BoardModel
import com.unibo.android.domain.repositories.BoardRepository
import com.unibo.android.data.remote.UnsplashApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BoardRepositoryImpl(
    context: Context,
    private val unsplash: UnsplashApi
): BoardRepository {
    private val boardDao = ChronioDatabase.getInstance(context).boardDao()

    override fun observeBoards(): Flow<List<BoardModel>> =
        boardDao.observeAll().map { list -> list.map { it.toModel() } }

    override suspend fun getById(id: Long): BoardModel? =
        boardDao.getById(id)?.toModel()

    override suspend fun save(board: BoardModel): Long {
        val withCover = if (board.coverImageUrl == null) {
            board.copy(coverImageUrl = fetchCover(board.title))
        } else board

        return boardDao.insert(withCover.toEntity())
    }

    override suspend fun update(board: BoardModel) =
        boardDao.update(board.toEntity())

    override suspend fun delete(board: BoardModel) =
        boardDao.delete(board.toEntity())

    private fun BoardEntity.toModel() =
        BoardModel(id, title, description, coverImageUrl, createdAt)

    private fun BoardModel.toEntity() =
        BoardEntity(id, title, description, coverImageUrl, createdAt)

    private suspend fun fetchCover(query: String): String? = runCatching {
        unsplash.searchPhotos(query).results.firstOrNull()?.urls?.regular
    }.getOrNull()
}