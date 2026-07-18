package com.unibo.android.data.di

import android.content.Context
import com.unibo.android.data.repository.EventRepositoryImpl
import com.unibo.android.data.repository.HolidayRepositoryImpl
import com.unibo.android.data.repository.TagRepositoryImpl
import com.unibo.android.data.repository.WeatherRepositoryImpl
import com.unibo.android.domain.di.RepositoryProvider
import com.unibo.android.domain.repositories.EventRepository
import com.unibo.android.domain.repositories.HolidayRepository
import com.unibo.android.domain.repositories.TagRepository
import com.unibo.android.domain.repositories.WeatherRepository

class RepositoryProviderImpl(context: Context) : RepositoryProvider {
    override val eventRepository: EventRepository = EventRepositoryImpl(context)
    override val tagRepository: TagRepository = TagRepositoryImpl(context)
    override val weatherRepository: WeatherRepository = WeatherRepositoryImpl(context)
    override val holidayRepository: HolidayRepository = HolidayRepositoryImpl()
}
