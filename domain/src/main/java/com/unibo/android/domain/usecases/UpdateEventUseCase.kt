package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

interface UpdateEventUseCase {
    suspend operator fun invoke(event: EventModel, tagIds: List<Long>): Result<Unit>
}

class UpdateEventUseCaseImpl(
    private val eventRepository: EventRepository
) : UpdateEventUseCase {
    override suspend operator fun invoke(event: EventModel, tagIds: List<Long>): Result<Unit> {
        if (event.title.isBlank()) return Result.failure(Exception("Title cannot be empty"))
        return try {
            eventRepository.update(event)
            eventRepository.setTags(event.id, tagIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
