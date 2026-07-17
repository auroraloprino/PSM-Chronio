package com.unibo.android.data.repository

import com.unibo.android.data.BuildConfig
import com.unibo.android.data.remote.UnsplashApi
import com.unibo.android.data.remote.model.UnsplashPhoto
import com.unibo.android.domain.models.PhotoModel
import com.unibo.android.domain.repositories.PhotoRepository
import retrofit2.HttpException
import java.io.IOException

class PhotoRepositoryImpl(
    private val api: UnsplashApi
) : PhotoRepository {

    override suspend fun searchPhotos(query: String): Result<List<PhotoModel>> {
        if (BuildConfig.UNSPLASH_ACCESS_KEY.isBlank()) {
            return Result.failure(
                IllegalStateException("Chiave Unsplash mancante. Aggiungi UNSPLASH_ACCESS_KEY in local.properties.")
            )
        }

        return try {
            val response = api.searchPhotos(query)
            Result.success(response.results.map { it.toModel() })
        } catch (e: HttpException) {
            // Traduciamo i codici HTTP in messaggi comprensibili
            val message = when (e.code()) {
                401 -> "Chiave Unsplash non valida."
                403 -> "Limite di richieste raggiunto (50/ora). Riprova più tardi."
                else -> "Errore del server (${e.code()})."
            }
            Result.failure(Exception(message, e))
        } catch (e: IOException) {
            Result.failure(Exception("Nessuna connessione a internet.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private fun UnsplashPhoto.toModel() = PhotoModel(
        id = id,
        thumbnailUrl = urls.thumb,
        fullUrl = urls.regular,
        authorName = user.name,
        authorProfileUrl = user.links.html
    )
}