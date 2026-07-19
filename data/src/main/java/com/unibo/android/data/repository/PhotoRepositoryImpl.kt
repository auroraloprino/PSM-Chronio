package com.unibo.android.data.repository

import com.unibo.android.data.remote.PicsumApi
import com.unibo.android.data.remote.model.PicsumPhoto
import com.unibo.android.domain.models.PhotoModel
import com.unibo.android.domain.repositories.PhotoRepository
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.abs

class PhotoRepositoryImpl(
    private val api: PicsumApi
) : PhotoRepository {

    override suspend fun searchPhotos(query: String): Result<List<PhotoModel>> {
        return try {
            // Picsum non supporta la ricerca per parola chiave: la query viene usata
            // come seed per scegliere una pagina, così la stessa ricerca dà sempre
            // lo stesso risultato mentre query diverse mostrano foto diverse.
            val page = (abs(query.hashCode()) % PAGE_COUNT) + 1
            val response = api.listPhotos(page = page, limit = PAGE_SIZE)
            Result.success(response.map { it.toModel() })
        } catch (e: HttpException) {
            Result.failure(Exception("Errore del server (${e.code()}).", e))
        } catch (e: IOException) {
            Result.failure(Exception("Nessuna connessione a internet.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun PicsumPhoto.toModel() = PhotoModel(
        id = id,
        thumbnailUrl = "https://picsum.photos/id/$id/300/200",
        fullUrl = "https://picsum.photos/id/$id/1080/720",
        authorName = author,
        authorProfileUrl = url
    )

    private companion object {
        const val PAGE_SIZE = 30
        const val PAGE_COUNT = 30
    }
}
