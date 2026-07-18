package com.unibo.android.chronio

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.unibo.android.domain.models.EventModel
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun schedule(context: Context, event: EventModel) {
        val triggerAt = if (event.allDay) {
            val cal = java.util.Calendar.getInstance().apply {
                timeInMillis = event.startTime
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        } else {
            if (event.reminderMinutes <= 0) return
            event.startTime - event.reminderMinutes * 60_000L
        }
        val delay = triggerAt - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder()
            .putString(ReminderWorker.KEY_TITLE, event.title)
            .putLong(ReminderWorker.KEY_START_TIME, event.startTime)
            .putInt(ReminderWorker.KEY_EVENT_ID, event.id.toInt())
            .putBoolean(ReminderWorker.KEY_ALL_DAY, event.allDay)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(tagFor(event.id))
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    fun cancel(context: Context, eventId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tagFor(eventId))
    }

    private fun tagFor(eventId: Long) = "reminder_$eventId"
}
