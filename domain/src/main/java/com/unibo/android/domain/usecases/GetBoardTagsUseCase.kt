package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.repositories.BoardTagRepository
import kotlinx.coroutines.flow.Flow

interface GetBoardTagsUseCase {
    operator fun invoke(): Flow<List<BoardTagModel>>
}

class GetBoardTagsUseCaseImpl(
    private val boardTagRepository: BoardTagRepository
) : GetBoardTagsUseCase {
    override fun invoke(): Flow<List<BoardTagModel>> = boardTagRepository.observeAll()
}