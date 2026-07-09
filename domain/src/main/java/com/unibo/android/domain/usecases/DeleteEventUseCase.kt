package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

interface DeleteEventUseCase {
    suspend operator fun invoke(event: EventModel): Result<Unit>
}

class DeleteEventUseCaseImpl(
    private val eventRepository: EventRepository
) : DeleteEventUseCase {
    override suspend operator fun invoke(event: EventModel): Result<Unit> =
        try {
            eventRepository.delete(event)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
