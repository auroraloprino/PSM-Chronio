package com.unibo.android.domain.models

data class EventModel(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val reminderMinutes: Int = 30,
    val tagIds: List<Long> = emptyList()
)
