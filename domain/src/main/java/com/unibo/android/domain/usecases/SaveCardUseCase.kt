package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.repositories.CardRepository

interface SaveCardUseCase {
    suspend operator fun invoke(card: CardModel, tagIds: List<Long>): Result<Unit>
}

class SaveCardUseCaseImpl(
    private val cardRepository: CardRepository
) : SaveCardUseCase {
    override suspend operator fun invoke(card: CardModel, tagIds: List<Long>): Result<Unit> {
        if (card.title.isBlank()) return Result.failure(Exception("Card title cannot be empty"))
        return try {
            val position =
                if (card.id == 0L) cardRepository.nextPosition(card.columnId) else card.position
            val id = cardRepository.save(card.copy(position = position))
            cardRepository.setTags(if (card.id == 0L) id else card.id, tagIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}