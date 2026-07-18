package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.repositories.CardRepository

interface MoveCardUseCase {
    suspend fun reorderWithinColumn(orderedCards: List<CardModel>): Result<Unit>
    suspend fun moveToColumn(card: CardModel, targetColumnId: Long): Result<Unit>

    suspend fun moveToColumnAtPosition(
        card: CardModel,
        targetColumnId: Long,
        orderedTargetColumnCards: List<CardModel>
    ): Result<Unit>
}

class MoveCardUseCaseImpl(
    private val cardRepository: CardRepository
) : MoveCardUseCase {

    override suspend fun reorderWithinColumn(orderedCards: List<CardModel>): Result<Unit> =
        try {
            val renumbered = orderedCards.mapIndexed { index, c -> c.copy(position = index) }
            cardRepository.updateAll(renumbered)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun moveToColumn(card: CardModel, targetColumnId: Long): Result<Unit> =
        try {
            val newPosition = cardRepository.nextPosition(targetColumnId)
            cardRepository.moveToColumn(card.id, targetColumnId, newPosition)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun moveToColumnAtPosition(
        card: CardModel,
        targetColumnId: Long,
        orderedTargetColumnCards: List<CardModel>
    ): Result<Unit> =
        try {
            val movedCard = card.copy(columnId = targetColumnId)
            val renumbered = orderedTargetColumnCards.mapIndexed { index, c ->
                (if (c.id == card.id) movedCard else c).copy(position = index)
            }
            cardRepository.updateAll(renumbered)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}