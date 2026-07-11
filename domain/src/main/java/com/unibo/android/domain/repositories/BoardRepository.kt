package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.BoardModel
import kotlinx.coroutines.flow.Flow

interface BoardRepository {
    fun observeBoards(): Flow<List<BoardModel>>
    suspend fun getById(id: Long): BoardModel?
    suspend fun save(board: BoardModel): Long
    suspend fun update(board: BoardModel)
    suspend fun delete(board: BoardModel)
}