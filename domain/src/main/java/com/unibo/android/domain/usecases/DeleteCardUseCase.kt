package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.repositories.CardRepository

interface DeleteCardUseCase {
    suspend operator fun invoke(card: CardModel): Result<Unit>
}

class DeleteCardUseCaseImpl(
    private val cardRepository: CardRepository
) : DeleteCardUseCase {
    override suspend operator fun invoke(card: CardModel): Result<Unit> =
        try {
            cardRepository.delete(card)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}