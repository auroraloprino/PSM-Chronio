package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.CardModel
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun observeCards(columnId: Long): Flow<List<CardModel>>
    suspend fun nextPosition(columnId: Long): Int
    suspend fun save(card: CardModel): Long
    suspend fun update(card: CardModel)
    suspend fun updateAll(cards: List<CardModel>)
    suspend fun moveToColumn(cardId: Long, targetColumnId: Long, newPosition: Int)
    suspend fun delete(card: CardModel)
    suspend fun setTags(cardId: Long, tagIds: List<Long>)
    suspend fun setDone(cardId: Long, isDone: Boolean)
}