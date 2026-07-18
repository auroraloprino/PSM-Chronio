package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.TagModel

interface TagRepository {
    suspend fun getAll(): List<TagModel>
    suspend fun getForEvent(eventId: Long): List<TagModel>
    suspend fun save(tag: TagModel): Long
    suspend fun update(tag: TagModel)
    suspend fun delete(tag: TagModel)
}
