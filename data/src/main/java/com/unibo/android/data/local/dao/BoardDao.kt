package com.unibo.android.data.local.dao

import androidx.room.*
import com.unibo.android.data.local.entity.BoardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(board: BoardEntity): Long

    @Update
    suspend fun update(board: BoardEntity)

    @Delete
    suspend fun delete(board: BoardEntity)

    @Query("SELECT * FROM boards ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BoardEntity>>

    @Query("SELECT * FROM boards WHERE id = :id")
    suspend fun getById(id: Long): BoardEntity?
}