package com.unibo.android.data.repository

import android.content.Context
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.TagEntity
import com.unibo.android.domain.models.TagModel
import com.unibo.android.domain.repositories.TagRepository

class TagRepositoryImpl(context: Context) : TagRepository {
    private val tagDao = ChronioDatabase.getInstance(context).tagDao()

    override suspend fun getAll(): List<TagModel> =
        tagDao.getAll().map { it.toModel() }

    override suspend fun getForEvent(eventId: Long): List<TagModel> =
        tagDao.getForEvent(eventId).map { it.toModel() }

    override suspend fun save(tag: TagModel): Long =
        tagDao.insert(tag.toEntity())

    override suspend fun update(tag: TagModel) {
        tagDao.insert(tag.toEntity())
    }

    override suspend fun delete(tag: TagModel) =
        tagDao.delete(tag.toEntity())

    private fun TagEntity.toModel() = TagModel(id, name, color, isSystem)
    private fun TagModel.toEntity() = TagEntity(id, name, color, isSystem)
}
