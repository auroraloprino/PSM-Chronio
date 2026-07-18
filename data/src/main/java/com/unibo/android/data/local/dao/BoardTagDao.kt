package com.unibo.android.data.local.dao

import androidx.room.*
import com.unibo.android.data.local.entity.BoardTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardTagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: BoardTagEntity): Long

    @Update
    suspend fun update(tag: BoardTagEntity)

    @Delete
    suspend fun delete(tag: BoardTagEntity)

    @Query("SELECT * FROM board_tags ORDER BY name ASC")
    fun observeAll(): Flow<List<BoardTagEntity>>
}