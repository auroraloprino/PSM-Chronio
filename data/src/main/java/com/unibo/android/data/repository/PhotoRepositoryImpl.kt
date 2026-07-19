package com.unibo.android.data.repository

import com.unibo.android.data.remote.WikimediaApi
import com.unibo.android.data.remote.model.WikimediaPage
import com.unibo.android.domain.models.PhotoModel
import com.unibo.android.domain.repositories.PhotoRepository
import retrofit2.HttpException
import java.io.IOException

class PhotoRepositoryImpl(
    private val api: WikimediaApi
) : PhotoRepository {

    override suspend fun searchPhotos(query: String): Result<List<PhotoModel>> {
        return try {
            val response = api.searchImages(search = "$query filetype:bitmap|drawing")
            val photos = response.query?.pages.orEmpty()
                .mapNotNull { it.toModel() }
            Result.success(photos)
        } catch (e: HttpException) {
            Result.failure(Exception("Errore del server (${e.code()}).", e))
        } catch (e: IOException) {
            Result.failure(Exception("Nessuna connessione a internet.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun WikimediaPage.toModel(): PhotoModel? {
        val info = imageinfo.firstOrNull() ?: return null
        val authorName = info.extmetadata?.artist?.value
            ?.replace(HTML_TAG_REGEX, "")
            ?.trim()
            ?.ifBlank { "Wikimedia Commons" }
            ?: "Wikimedia Commons"

        return PhotoModel(
            id = pageid.toString(),
            thumbnailUrl = info.thumburl.ifBlank { info.url },
            fullUrl = info.url,
            authorName = authorName,
            authorProfileUrl = info.descriptionurl
        )
    }

    private companion object {
        val HTML_TAG_REGEX = Regex("<[^>]*>")
    }
}
