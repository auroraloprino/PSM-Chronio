package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

interface GetEventsUseCase {
    suspend operator fun invoke(): List<EventModel>
}

class GetEventsUseCaseImpl(
    private val eventRepository: EventRepository
) : GetEventsUseCase {
    override suspend operator fun invoke(): List<EventModel> =
        eventRepository.getAll()
}
