package com.unibo.android.domain.di

import com.unibo.android.domain.repositories.BoardRepository
import com.unibo.android.domain.repositories.BoardTagRepository
import com.unibo.android.domain.repositories.CardRepository
import com.unibo.android.domain.repositories.ColumnRepository
import com.unibo.android.domain.repositories.EventRepository
import com.unibo.android.domain.repositories.HolidayRepository
import com.unibo.android.domain.repositories.PhotoRepository
import com.unibo.android.domain.repositories.TagRepository
import com.unibo.android.domain.repositories.WeatherRepository

interface RepositoryProvider {
    val eventRepository: EventRepository
    val tagRepository: TagRepository
    val weatherRepository: WeatherRepository
    val holidayRepository: HolidayRepository
    val boardRepository: BoardRepository
    val columnRepository: ColumnRepository
    val cardRepository: CardRepository
    val boardTagRepository: BoardTagRepository
    val photoRepository: PhotoRepository
}
