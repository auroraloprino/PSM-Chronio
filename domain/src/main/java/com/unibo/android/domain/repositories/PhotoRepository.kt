package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.PhotoModel

interface PhotoRepository {
    suspend fun searchPhotos(query: String): Result<List<PhotoModel>>
}