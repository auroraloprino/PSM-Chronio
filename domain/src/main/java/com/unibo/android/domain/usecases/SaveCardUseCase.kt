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
            // save() fa un insert con onConflict=REPLACE: su una card esistente cancella e
            // ricrea la riga, con relativo CASCADE sulle relazioni. Su una modifica va usato
            // update(), che fa un UPDATE SQL vero senza toccare le righe collegate.
            val cardId: Long
            if (card.id == 0L) {
                val position = cardRepository.nextPosition(card.columnId)
                cardId = cardRepository.save(card.copy(position = position))
            } else {
                cardRepository.update(card)
                cardId = card.id
            }
            cardRepository.setTags(cardId, tagIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}