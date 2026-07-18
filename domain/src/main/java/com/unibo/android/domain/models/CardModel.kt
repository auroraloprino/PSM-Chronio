package com.unibo.android.domain.models

data class CardModel(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val columnId: Long,
    val position: Int,
    val tags: List<BoardTagModel> = emptyList(),
    val isDone: Boolean = false
)