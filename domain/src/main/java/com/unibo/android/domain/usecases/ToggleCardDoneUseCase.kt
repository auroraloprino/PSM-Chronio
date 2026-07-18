package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.repositories.CardRepository

interface ToggleCardDoneUseCase {
    suspend operator fun invoke(card: CardModel): Result<Unit>
}

class ToggleCardDoneUseCaseImpl(
    private val cardRepository: CardRepository
) : ToggleCardDoneUseCase {
    override suspend operator fun invoke(card: CardModel): Result<Unit> =
        try {
            cardRepository.setDone(card.id, !card.isDone)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
