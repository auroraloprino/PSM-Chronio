package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.EventEntity
import com.unibo.android.data.local.entity.EventTagCrossRef
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository

class EventRepositoryImpl(context: Context) : EventRepository {
    private val eventDao = ChronioDatabase.getInstance(context).eventDao()

    override suspend fun getAll(): List<EventModel> =
        eventDao.getAll().map { it.event.toModel() }

    override suspend fun getInRange(startMs: Long, endMs: Long): List<EventModel> =
        eventDao.getInRange(startMs, endMs).map { it.event.toModel() }

    override suspend fun getById(id: Long): EventModel? =
        eventDao.getById(id)?.event?.toModel()

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

    private fun EventEntity.toModel() = EventModel(id, title, description, startTime, endTime, reminderMinutes)
    private fun EventModel.toEntity() = EventEntity(id, title, description, startTime, endTime, reminderMinutes)
}
