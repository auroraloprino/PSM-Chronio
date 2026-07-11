package com.unibo.android.domain.di

import com.unibo.android.domain.usecases.AddColumnUseCase
import com.unibo.android.domain.usecases.AddColumnUseCaseImpl
import com.unibo.android.domain.usecases.DeleteColumnUseCase
import com.unibo.android.domain.usecases.DeleteColumnUseCaseImpl
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
import com.unibo.android.domain.usecases.SaveBoardUseCase
import com.unibo.android.domain.usecases.SaveBoardUseCaseImpl
import com.unibo.android.domain.usecases.SaveCardUseCase
import com.unibo.android.domain.usecases.SaveCardUseCaseImpl

object UseCasesProvider {
    lateinit var getBoardsUseCase: GetBoardsUseCase
    lateinit var saveBoardUseCase: SaveBoardUseCase
    lateinit var getColumnsUseCase: GetColumnsUseCase
    lateinit var addColumnUseCase: AddColumnUseCase
    lateinit var deleteColumnUseCase: DeleteColumnUseCase
    lateinit var reorderColumnsUseCase: ReorderColumnsUseCase
    lateinit var getCardsUseCase: GetCardsUseCase
    lateinit var saveCardUseCase: SaveCardUseCase
    lateinit var moveCardUseCase: MoveCardUseCase

    fun setup(repositoryProvider: RepositoryProvider) {
        getBoardsUseCase = GetBoardsUseCaseImpl(repositoryProvider.boardRepository)
        saveBoardUseCase = SaveBoardUseCaseImpl(repositoryProvider.boardRepository)
        getColumnsUseCase = GetColumnsUseCaseImpl(repositoryProvider.columnRepository)
        addColumnUseCase = AddColumnUseCaseImpl(repositoryProvider.columnRepository)
        deleteColumnUseCase = DeleteColumnUseCaseImpl(repositoryProvider.columnRepository)
        reorderColumnsUseCase = ReorderColumnsUseCaseImpl(repositoryProvider.columnRepository)
        getCardsUseCase = GetCardsUseCaseImpl(repositoryProvider.cardRepository)
        saveCardUseCase = SaveCardUseCaseImpl(repositoryProvider.cardRepository)
        moveCardUseCase = MoveCardUseCaseImpl(repositoryProvider.cardRepository)
    }
}