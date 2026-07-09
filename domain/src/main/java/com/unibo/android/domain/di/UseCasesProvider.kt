package com.unibo.android.domain.di

import com.unibo.android.domain.usecases.DeleteEventUseCase
import com.unibo.android.domain.usecases.DeleteEventUseCaseImpl
import com.unibo.android.domain.usecases.GetEventsInRangeUseCase
import com.unibo.android.domain.usecases.GetEventsInRangeUseCaseImpl
import com.unibo.android.domain.usecases.GetEventsUseCase
import com.unibo.android.domain.usecases.GetEventsUseCaseImpl
import com.unibo.android.domain.usecases.GetTagsUseCase
import com.unibo.android.domain.usecases.GetTagsUseCaseImpl
import com.unibo.android.domain.usecases.SaveEventUseCase
import com.unibo.android.domain.usecases.SaveEventUseCaseImpl
import com.unibo.android.domain.usecases.SaveTagUseCase
import com.unibo.android.domain.usecases.SaveTagUseCaseImpl
import com.unibo.android.domain.usecases.UpdateEventUseCase
import com.unibo.android.domain.usecases.UpdateEventUseCaseImpl

object UseCasesProvider {
    lateinit var getEventsUseCase: GetEventsUseCase
    lateinit var getEventsInRangeUseCase: GetEventsInRangeUseCase
    lateinit var saveEventUseCase: SaveEventUseCase
    lateinit var updateEventUseCase: UpdateEventUseCase
    lateinit var deleteEventUseCase: DeleteEventUseCase
    lateinit var getTagsUseCase: GetTagsUseCase
    lateinit var saveTagUseCase: SaveTagUseCase

    fun setup(repositoryProvider: RepositoryProvider) {
        getEventsUseCase = GetEventsUseCaseImpl(repositoryProvider.eventRepository)
        getEventsInRangeUseCase = GetEventsInRangeUseCaseImpl(repositoryProvider.eventRepository)
        saveEventUseCase = SaveEventUseCaseImpl(repositoryProvider.eventRepository)
        updateEventUseCase = UpdateEventUseCaseImpl(repositoryProvider.eventRepository)
        deleteEventUseCase = DeleteEventUseCaseImpl(repositoryProvider.eventRepository)
        getTagsUseCase = GetTagsUseCaseImpl(repositoryProvider.tagRepository)
        saveTagUseCase = SaveTagUseCaseImpl(repositoryProvider.tagRepository)
    }
}
