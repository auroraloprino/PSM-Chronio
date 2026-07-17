package com.unibo.android.domain.notifications

import com.unibo.android.domain.models.EventModel

interface ReminderScheduler {
    fun schedule(event: EventModel)
    fun cancel(eventId: Long)
}
