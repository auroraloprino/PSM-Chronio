package com.unibo.android.data.local.dao

import androidx.room.*
import com.unibo.android.data.local.entity.EventEntity
import com.unibo.android.data.local.entity.EventTagCrossRef
import com.unibo.android.data.local.entity.EventWithTags

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Transaction
    @Query("SELECT * FROM events ORDER BY startTime ASC")
    suspend fun getAll(): List<EventWithTags>

    @Transaction
    @Query("SELECT * FROM events WHERE startTime >= :startMs AND startTime <= :endMs ORDER BY startTime ASC")
    suspend fun getInRange(startMs: Long, endMs: Long): List<EventWithTags>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: Long): EventWithTags?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: EventTagCrossRef)

    @Query("DELETE FROM event_tag_cross_ref WHERE eventId = :eventId")
    suspend fun deleteCrossRefsForEvent(eventId: Long)
}
