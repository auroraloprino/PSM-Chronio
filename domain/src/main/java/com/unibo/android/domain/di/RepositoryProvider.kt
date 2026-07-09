package com.unibo.android.domain.di

import com.unibo.android.domain.repositories.EventRepository
import com.unibo.android.domain.repositories.TagRepository

interface RepositoryProvider {
    val eventRepository: EventRepository
    val tagRepository: TagRepository
}
