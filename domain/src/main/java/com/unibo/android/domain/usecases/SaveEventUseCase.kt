package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

interface SaveEventUseCase {
    suspend operator fun invoke(event: EventModel, tagIds: List<Long>): Result<Unit>
}

class SaveEventUseCaseImpl(
    private val eventRepository: EventRepository
) : SaveEventUseCase {
    override suspend operator fun invoke(event: EventModel, tagIds: List<Long>): Result<Unit> {
        if (event.title.isBlank()) return Result.failure(Exception("Title cannot be empty"))
        return try {
            val id = eventRepository.save(event)
            eventRepository.setTags(id, tagIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
