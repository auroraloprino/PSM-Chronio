package com.unibo.android.domain.models

data class PhotoModel(
    val id: String,
    val thumbnailUrl: String,   // per la griglia (leggera)
    val fullUrl: String,        // da salvare come coverImageUrl
    val authorName: String,
    val authorProfileUrl: String
)