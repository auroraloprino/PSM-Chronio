package com.unibo.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class UnsplashSearchResponse(
    val total: Int,
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val urls: UnsplashUrls,
    val user: UnsplashUser
)

data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    val name: String,
    @SerializedName("links") val links: UnsplashUserLinks
)

data class UnsplashUserLinks(
    val html: String
)