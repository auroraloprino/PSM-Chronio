package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

interface SaveEventUseCase {
    suspend operator fun invoke(event: EventModel, tagIds: List<Long>): Long
}

class SaveEventUseCaseImpl(
    private val eventRepository: EventRepository
) : SaveEventUseCase {
    override suspend operator fun invoke(event: EventModel, tagIds: List<Long>): Long {
        val id = eventRepository.save(event)
        eventRepository.setTags(id, tagIds)
        return id
    }
}
