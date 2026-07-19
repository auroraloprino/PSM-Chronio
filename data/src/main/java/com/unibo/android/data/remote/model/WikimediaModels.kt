package com.unibo.android.data.remote.model

import com.google.gson.annotations.SerializedName

data class WikimediaSearchResponse(
    val query: WikimediaQuery? = null
)

data class WikimediaQuery(
    val pages: List<WikimediaPage> = emptyList()
)

data class WikimediaPage(
    val pageid: Long = 0,
    val title: String = "",
    val imageinfo: List<WikimediaImageInfo> = emptyList()
)

data class WikimediaImageInfo(
    val url: String = "",
    val descriptionurl: String = "",
    val thumburl: String = "",
    val extmetadata: WikimediaExtMetadata? = null
)

data class WikimediaExtMetadata(
    @SerializedName("Artist")
    val artist: WikimediaMetaValue? = null
)

data class WikimediaMetaValue(
    val value: String = ""
)
