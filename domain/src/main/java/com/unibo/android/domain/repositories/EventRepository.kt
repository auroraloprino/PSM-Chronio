package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.EventModel

interface EventRepository {
    suspend fun getAll(): List<EventModel>
    suspend fun getInRange(startMs: Long, endMs: Long): List<EventModel>
    suspend fun getById(id: Long): EventModel?
    suspend fun save(event: EventModel): Long
    suspend fun update(event: EventModel)
    suspend fun delete(event: EventModel)
    suspend fun setTags(eventId: Long, tagIds: List<Long>)
}
