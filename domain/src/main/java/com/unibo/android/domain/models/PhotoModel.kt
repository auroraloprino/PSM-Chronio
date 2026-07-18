package com.unibo.android.domain.models

data class PhotoModel(
    val id: String,
    val thumbnailUrl: String,
    val fullUrl: String,
    val authorName: String,
    val authorProfileUrl: String
)