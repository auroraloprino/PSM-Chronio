package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.PhotoModel
import com.unibo.android.domain.repositories.PhotoRepository

interface SearchPhotosUseCase {
    suspend operator fun invoke(query: String): Result<List<PhotoModel>>
}

class SearchPhotosUseCaseImpl(
    private val photoRepository: PhotoRepository
) : SearchPhotosUseCase {

    override suspend operator fun invoke(query: String): Result<List<PhotoModel>> {
        if (query.isBlank()) return Result.success(emptyList())
        return photoRepository.searchPhotos(query.trim())
    }
}