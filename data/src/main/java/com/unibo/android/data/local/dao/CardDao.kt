package com.unibo.android.data.local.dao

import androidx.room.*
import com.unibo.android.data.local.entity.CardEntity
import com.unibo.android.data.local.entity.CardTagCrossRef
import com.unibo.android.data.local.entity.CardWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity)

    @Update
    suspend fun updateAll(cards: List<CardEntity>)

    @Delete
    suspend fun delete(card: CardEntity)
    @Transaction
    @Query("SELECT * FROM cards WHERE columnId = :columnId ORDER BY position ASC")
    fun observeForColumn(columnId: Long): Flow<List<CardWithTags>>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM cards WHERE columnId = :columnId")
    suspend fun nextPosition(columnId: Long): Int

    @Query("UPDATE cards SET columnId = :targetColumnId, position = :newPosition WHERE id = :cardId")
    suspend fun moveToColumn(cardId: Long, targetColumnId: Long, newPosition: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(refs: List<CardTagCrossRef>)

    @Query("DELETE FROM card_tag_cross_ref WHERE cardId = :cardId")
    suspend fun deleteCrossRefsForCard(cardId: Long)

    @Transaction
    suspend fun replaceTagsForCard(cardId: Long, refs: List<CardTagCrossRef>) {
        deleteCrossRefsForCard(cardId)
        if (refs.isNotEmpty()) insertCrossRefs(refs)
    }
}