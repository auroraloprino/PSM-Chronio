package com.unibo.android.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "event_tag_cross_ref",
    primaryKeys = ["eventId", "tagId"]
)
data class EventTagCrossRef(
    val eventId: Long,
    val tagId: Long
)
