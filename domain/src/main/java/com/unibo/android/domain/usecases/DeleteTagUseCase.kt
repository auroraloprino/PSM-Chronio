package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.TagModel
import com.unibo.android.domain.repositories.TagRepository

interface DeleteTagUseCase {
    suspend operator fun invoke(tag: TagModel)
}

class DeleteTagUseCaseImpl(
    private val tagRepository: TagRepository
) : DeleteTagUseCase {
    override suspend operator fun invoke(tag: TagModel) = tagRepository.delete(tag)
}
