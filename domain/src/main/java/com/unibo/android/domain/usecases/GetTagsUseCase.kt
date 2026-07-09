package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.TagModel
import com.unibo.android.domain.repositories.TagRepository

interface GetTagsUseCase {
    suspend operator fun invoke(): List<TagModel>
}

class GetTagsUseCaseImpl(
    private val tagRepository: TagRepository
) : GetTagsUseCase {
    override suspend operator fun invoke(): List<TagModel> =
        tagRepository.getAll()
}
