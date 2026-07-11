package com.unibo.android.domain.models

data class ColumnModel(
    val id: Long = 0,
    val title: String,
    val position: Int,
    val boardId: Long
)