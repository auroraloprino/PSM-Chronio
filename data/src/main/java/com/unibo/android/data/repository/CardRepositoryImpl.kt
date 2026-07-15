package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.CardEntity
import com.unibo.android.data.local.entity.CardTagCrossRef
import com.unibo.android.data.local.entity.CardWithTags
import com.unibo.android.data.local.entity.TagEntity
import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.repositories.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CardRepositoryImpl(context: Context) : CardRepository {
    private val cardDao = ChronioDatabase.getInstance(context).cardDao()

    override fun observeCards(columnId: Long): Flow<List<CardModel>> =
        cardDao.observeForColumn(columnId).map { list -> list.map { it.toModel() } }

    override suspend fun nextPosition(columnId: Long): Int =
        cardDao.nextPosition(columnId)

    override suspend fun save(card: CardModel): Long =
        cardDao.insert(card.toEntity())

    override suspend fun update(card: CardModel) =
        cardDao.update(card.toEntity())

    override suspend fun updateAll(cards: List<CardModel>) =
        cardDao.updateAll(cards.map { it.toEntity() })

    override suspend fun moveToColumn(cardId: Long, targetColumnId: Long, newPosition: Int) =
        cardDao.moveToColumn(cardId, targetColumnId, newPosition)

    override suspend fun delete(card: CardModel) =
        cardDao.delete(card.toEntity())

    override suspend fun setTags(cardId: Long, tagIds: List<Long>) {
        cardDao.deleteCrossRefsForCard(cardId)
        tagIds.forEach { cardDao.insertCrossRef(CardTagCrossRef(cardId, it)) }
    }

    // mappers
    private fun CardWithTags.toModel() = CardModel(
        id = card.id,
        title = card.title,
        description = card.description,
        columnId = card.columnId,
        position = card.position,
        tags = tags.map { it.toModel() }
    )

    private fun CardModel.toEntity() = CardEntity(
        id = id,
        title = title,
        description = description,
        columnId = columnId,
        position = position
    )

    private fun TagEntity.toModel() = BoardTagModel(id, name, color)
}