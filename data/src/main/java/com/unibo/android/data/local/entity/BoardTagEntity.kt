package com.unibo.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "board_tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class BoardTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String = "#6200EE"
)