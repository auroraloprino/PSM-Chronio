package com.unibo.android.chronio

import android.content.Context
import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.notifications.ReminderScheduler

class ReminderSchedulerImpl(private val context: Context) : ReminderScheduler {
    override fun schedule(event: EventModel) = NotificationScheduler.schedule(context, event)
    override fun cancel(eventId: Long) = NotificationScheduler.cancel(context, eventId)
}
