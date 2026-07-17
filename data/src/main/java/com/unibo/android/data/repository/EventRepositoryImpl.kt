package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.EventEntity
import com.unibo.android.data.local.entity.EventTagCrossRef
import com.unibo.android.data.local.entity.EventWithTags
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

class EventRepositoryImpl(context: Context) : EventRepository {
    private val eventDao = ChronioDatabase.getInstance(context).eventDao()

    override suspend fun getAll(): List<EventModel> =
        eventDao.getAll().map { it.toModel() }

    override suspend fun getInRange(startMs: Long, endMs: Long): List<EventModel> =
        eventDao.getInRange(startMs, endMs).map { it.toModel() }

    override suspend fun getById(id: Long): EventModel? =
        eventDao.getById(id)?.toModel()

    override suspend fun save(event: EventModel): Long =
        eventDao.insert(event.toEntity())

    override suspend fun update(event: EventModel) =
        eventDao.update(event.toEntity())

    override suspend fun delete(event: EventModel) =
        eventDao.delete(event.toEntity())

    override suspend fun setTags(eventId: Long, tagIds: List<Long>) {
        eventDao.deleteCrossRefsForEvent(eventId)
        tagIds.forEach { eventDao.insertCrossRef(EventTagCrossRef(eventId, it)) }
    }

    private fun EventWithTags.toModel() = EventModel(event.id, event.title, event.description, event.startTime, event.endTime, event.allDay, event.reminderMinutes, tags.map { it.id })
    private fun EventModel.toEntity() = EventEntity(id, title, description, startTime, endTime, allDay, reminderMinutes)
}
