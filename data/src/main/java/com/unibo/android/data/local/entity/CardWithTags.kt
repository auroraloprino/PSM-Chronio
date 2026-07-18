package com.unibo.android.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CardWithTags(
    @Embedded val card: CardEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CardTagCrossRef::class,
            parentColumn = "cardId",
            entityColumn = "tagId"
        )
    )
    val tags: List<BoardTagEntity>
)