package com.unibo.android.domain.di

import com.unibo.android.domain.repositories.BoardRepository
import com.unibo.android.domain.repositories.CardRepository
import com.unibo.android.domain.repositories.ColumnRepository

interface RepositoryProvider {
    val boardRepository: BoardRepository
    val columnRepository: ColumnRepository
    val cardRepository: CardRepository
}