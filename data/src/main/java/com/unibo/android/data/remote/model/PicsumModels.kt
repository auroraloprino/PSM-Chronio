package com.unibo.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class PicsumPhoto(
    val id: String = "",
    val author: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val url: String = "",
    @SerializedName("download_url")
    val downloadUrl: String = ""
)
