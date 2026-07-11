package com.unibo.android.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "board_columns",
    foreignKeys = [ForeignKey(
        entity = BoardEntity::class,
        parentColumns = ["id"],
        childColumns = ["boardId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("boardId")]
)
data class ColumnEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val position: Int,
    val boardId: Long
)