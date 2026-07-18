package com.unibo.android.data.di

import android.content.Context
import com.unibo.android.data.remote.UnsplashApi
import com.unibo.android.data.repository.BoardRepositoryImpl
import com.unibo.android.data.repository.BoardTagRepositoryImpl
import com.unibo.android.data.repository.CardRepositoryImpl
import com.unibo.android.data.repository.ColumnRepositoryImpl
import com.unibo.android.data.repository.EventRepositoryImpl
import com.unibo.android.data.repository.HolidayRepositoryImpl
import com.unibo.android.data.repository.PhotoRepositoryImpl
import com.unibo.android.data.repository.TagRepositoryImpl
import com.unibo.android.data.repository.WeatherRepositoryImpl
import com.unibo.android.domain.di.RepositoryProvider
import com.unibo.android.domain.repositories.BoardRepository
import com.unibo.android.domain.repositories.BoardTagRepository
import com.unibo.android.domain.repositories.CardRepository
import com.unibo.android.domain.repositories.ColumnRepository
import com.unibo.android.domain.repositories.EventRepository
import com.unibo.android.domain.repositories.HolidayRepository
import com.unibo.android.domain.repositories.PhotoRepository
import com.unibo.android.domain.repositories.TagRepository
import com.unibo.android.domain.repositories.WeatherRepository

class RepositoryProviderImpl(context: Context) : RepositoryProvider {
    override val eventRepository: EventRepository = EventRepositoryImpl(context)
    override val tagRepository: TagRepository = TagRepositoryImpl(context)
    override val weatherRepository: WeatherRepository = WeatherRepositoryImpl(context)
    override val holidayRepository: HolidayRepository = HolidayRepositoryImpl()
    override val boardRepository: BoardRepository = BoardRepositoryImpl(context)
    override val columnRepository: ColumnRepository = ColumnRepositoryImpl(context)
    override val cardRepository: CardRepository = CardRepositoryImpl(context)
    override val boardTagRepository: BoardTagRepository = BoardTagRepositoryImpl(context)
    override val photoRepository: PhotoRepository = PhotoRepositoryImpl(UnsplashApi.create())
}
