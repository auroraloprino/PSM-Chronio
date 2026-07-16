package com.unibo.android.data.di

import android.content.Context
import com.unibo.android.data.repository.BoardRepositoryImpl
import com.unibo.android.data.repository.BoardTagRepositoryImpl
import com.unibo.android.data.repository.CardRepositoryImpl
import com.unibo.android.data.repository.ColumnRepositoryImpl
import com.unibo.android.domain.di.RepositoryProvider
import com.unibo.android.domain.repositories.BoardRepository
import com.unibo.android.domain.repositories.BoardTagRepository
import com.unibo.android.domain.repositories.CardRepository
import com.unibo.android.domain.repositories.ColumnRepository
import com.unibo.android.data.remote.UnsplashApi

class RepositoryProviderImpl(context: Context) : RepositoryProvider {
    override val boardRepository: BoardRepository = BoardRepositoryImpl(context, UnsplashApi.create())
    override val columnRepository: ColumnRepository = ColumnRepositoryImpl(context)
    override val cardRepository: CardRepository = CardRepositoryImpl(context)
    override val boardTagRepository: BoardTagRepository = BoardTagRepositoryImpl(context)
}
