package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.BoardTagModel
import kotlinx.coroutines.flow.Flow

interface BoardTagRepository {
    fun observeAll(): Flow<List<BoardTagModel>>
    suspend fun save(tag: BoardTagModel): Long
    suspend fun update(tag: BoardTagModel)
    suspend fun delete(tag: BoardTagModel)
}