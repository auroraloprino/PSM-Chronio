package com.unibo.android.data.remote.model

data class UnsplashSearchResponse(
    val total: Int = 0,
    val results: List<UnsplashPhoto> = emptyList()
)

data class UnsplashPhoto(
    val id: String,
    val urls: UnsplashUrls,
    val user: UnsplashUser
)

data class UnsplashUrls(
    val raw: String = "",
    val full: String = "",
    val regular: String = "",
    val small: String = "",
    val thumb: String = ""
)

data class UnsplashUser(
    val name: String = "",
    val links: UnsplashUserLinks = UnsplashUserLinks()
)

data class UnsplashUserLinks(
    val html: String = ""
)