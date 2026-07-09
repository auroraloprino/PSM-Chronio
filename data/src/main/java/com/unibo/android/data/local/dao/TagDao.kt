package com.unibo.android.data.local.dao

import androidx.room.*
import com.unibo.android.data.local.entity.TagEntity

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagEntity): Long

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("SELECT * FROM tags ORDER BY name ASC")
    suspend fun getAll(): List<TagEntity>

    @Query("""
        SELECT tags.* FROM tags
        INNER JOIN event_tag_cross_ref ON tags.id = event_tag_cross_ref.tagId
        WHERE event_tag_cross_ref.eventId = :eventId
    """)
    suspend fun getForEvent(eventId: Long): List<TagEntity>
}
