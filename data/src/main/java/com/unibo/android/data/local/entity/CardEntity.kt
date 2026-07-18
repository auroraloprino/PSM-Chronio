package com.unibo.android.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [ForeignKey(
        entity = ColumnEntity::class,
        parentColumns = ["id"],
        childColumns = ["columnId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("columnId")]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val columnId: Long,
    val position: Int,
    val isDone: Boolean = false
)