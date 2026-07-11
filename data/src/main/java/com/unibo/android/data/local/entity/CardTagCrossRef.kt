package com.unibo.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "card_tag_cross_ref",
    primaryKeys = ["cardId", "tagId"],
    indices = [Index("cardId"), Index("tagId")]
)
data class CardTagCrossRef(
    val cardId: Long,
    val tagId: Long
)