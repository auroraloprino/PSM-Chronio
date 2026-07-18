package com.unibo.android.domain.models

data class BoardModel(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val coverImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)