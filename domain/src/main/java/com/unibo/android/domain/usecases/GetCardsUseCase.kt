package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.repositories.CardRepository
import kotlinx.coroutines.flow.Flow

interface GetCardsUseCase {
    operator fun invoke(columnId: Long): Flow<List<CardModel>>
}

class GetCardsUseCaseImpl(
    private val cardRepository: CardRepository
) : GetCardsUseCase {
    override fun invoke(columnId: Long): Flow<List<CardModel>> =
        cardRepository.observeCards(columnId)
}