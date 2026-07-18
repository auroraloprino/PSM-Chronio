package com.unibo.android.data.local.dao

import androidx.room.*
import com.unibo.android.data.local.entity.ColumnEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColumnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(column: ColumnEntity): Long

    @Update
    suspend fun update(column: ColumnEntity)

    @Update
    suspend fun updateAll(columns: List<ColumnEntity>)

    @Delete
    suspend fun delete(column: ColumnEntity)

    @Query("SELECT * FROM board_columns WHERE boardId = :boardId ORDER BY position ASC")
    fun observeForBoard(boardId: Long): Flow<List<ColumnEntity>>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM board_columns WHERE boardId = :boardId")
    suspend fun nextPosition(boardId: Long): Int
}