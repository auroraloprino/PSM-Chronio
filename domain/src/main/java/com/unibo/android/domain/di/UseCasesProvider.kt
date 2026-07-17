package com.unibo.android.domain.di

import com.unibo.android.domain.usecases.AddColumnUseCase
import com.unibo.android.domain.usecases.AddColumnUseCaseImpl
import com.unibo.android.domain.usecases.DeleteBoardTagUseCase
import com.unibo.android.domain.usecases.DeleteBoardTagUseCaseImpl
import com.unibo.android.domain.usecases.DeleteBoardUseCase
import com.unibo.android.domain.usecases.DeleteBoardUseCaseImpl
import com.unibo.android.domain.usecases.DeleteCardUseCase
import com.unibo.android.domain.usecases.DeleteCardUseCaseImpl
import com.unibo.android.domain.usecases.DeleteColumnUseCase
import com.unibo.android.domain.usecases.DeleteColumnUseCaseImpl
import com.unibo.android.domain.usecases.GetBoardTagsUseCase
import com.unibo.android.domain.usecases.GetBoardTagsUseCaseImpl
import com.unibo.android.domain.usecases.GetBoardsUseCase
import com.unibo.android.domain.usecases.GetBoardsUseCaseImpl
import com.unibo.android.domain.usecases.GetCardsUseCase
import com.unibo.android.domain.usecases.GetCardsUseCaseImpl
import com.unibo.android.domain.usecases.GetColumnsUseCase
import com.unibo.android.domain.usecases.GetColumnsUseCaseImpl
import com.unibo.android.domain.usecases.MoveCardUseCase
import com.unibo.android.domain.usecases.MoveCardUseCaseImpl
import com.unibo.android.domain.usecases.ReorderColumnsUseCase
import com.unibo.android.domain.usecases.ReorderColumnsUseCaseImpl
import com.unibo.android.domain.usecases.SaveBoardTagUseCase
import com.unibo.android.domain.usecases.SaveBoardTagUseCaseImpl
import com.unibo.android.domain.usecases.SaveBoardUseCase
import com.unibo.android.domain.usecases.SaveBoardUseCaseImpl
import com.unibo.android.domain.usecases.SaveCardUseCase
import com.unibo.android.domain.usecases.SaveCardUseCaseImpl
import com.unibo.android.domain.usecases.SearchPhotosUseCase
import com.unibo.android.domain.usecases.SearchPhotosUseCaseImpl

object UseCasesProvider {
    lateinit var getBoardsUseCase: GetBoardsUseCase
    lateinit var saveBoardUseCase: SaveBoardUseCase
    lateinit var deleteBoardUseCase: DeleteBoardUseCase
    lateinit var getColumnsUseCase: GetColumnsUseCase
    lateinit var addColumnUseCase: AddColumnUseCase
    lateinit var deleteColumnUseCase: DeleteColumnUseCase
    lateinit var reorderColumnsUseCase: ReorderColumnsUseCase
    lateinit var getCardsUseCase: GetCardsUseCase
    lateinit var saveCardUseCase: SaveCardUseCase
    lateinit var moveCardUseCase: MoveCardUseCase
    lateinit var deleteCardUseCase: DeleteCardUseCase
    lateinit var getBoardTagsUseCase: GetBoardTagsUseCase
    lateinit var saveBoardTagUseCase: SaveBoardTagUseCase
    lateinit var deleteBoardTagUseCase: DeleteBoardTagUseCase
    lateinit var searchPhotosUseCase: SearchPhotosUseCase

    fun setup(repositoryProvider: RepositoryProvider) {
        getBoardsUseCase = GetBoardsUseCaseImpl(repositoryProvider.boardRepository)
        saveBoardUseCase = SaveBoardUseCaseImpl(repositoryProvider.boardRepository)
        deleteBoardUseCase = DeleteBoardUseCaseImpl(repositoryProvider.boardRepository)
        getColumnsUseCase = GetColumnsUseCaseImpl(repositoryProvider.columnRepository)
        addColumnUseCase = AddColumnUseCaseImpl(repositoryProvider.columnRepository)
        deleteColumnUseCase = DeleteColumnUseCaseImpl(repositoryProvider.columnRepository)
        reorderColumnsUseCase = ReorderColumnsUseCaseImpl(repositoryProvider.columnRepository)
        getCardsUseCase = GetCardsUseCaseImpl(repositoryProvider.cardRepository)
        saveCardUseCase = SaveCardUseCaseImpl(repositoryProvider.cardRepository)
        moveCardUseCase = MoveCardUseCaseImpl(repositoryProvider.cardRepository)
        deleteCardUseCase = DeleteCardUseCaseImpl(repositoryProvider.cardRepository)
        getBoardTagsUseCase = GetBoardTagsUseCaseImpl(repositoryProvider.boardTagRepository)
        saveBoardTagUseCase = SaveBoardTagUseCaseImpl(repositoryProvider.boardTagRepository)
        deleteBoardTagUseCase = DeleteBoardTagUseCaseImpl(repositoryProvider.boardTagRepository)
        searchPhotosUseCase = SearchPhotosUseCaseImpl(repositoryProvider.photoRepository)
    }
}