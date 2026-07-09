package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

interface GetEventsInRangeUseCase {
    suspend operator fun invoke(startMs: Long, endMs: Long): List<EventModel>
}

class GetEventsInRangeUseCaseImpl(
    private val eventRepository: EventRepository
) : GetEventsInRangeUseCase {
    override suspend operator fun invoke(startMs: Long, endMs: Long): List<EventModel> =
        eventRepository.getInRange(startMs, endMs)
}
