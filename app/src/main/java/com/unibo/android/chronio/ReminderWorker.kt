package com.unibo.android.chronio

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: return Result.success()
        val startTime = inputData.getLong(KEY_START_TIME, 0L)
        val allDay = inputData.getBoolean(KEY_ALL_DAY, false)
        val contentText = if (allDay) "Evento tutto il giorno"
        else {
            val timeStr = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(startTime)
            "Inizia alle $timeStr"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(inputData.getInt(KEY_EVENT_ID, 0), notification)
        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "chronio_reminders"
        const val KEY_TITLE = "title"
        const val KEY_START_TIME = "start_time"
        const val KEY_ALL_DAY = "all_day"
        const val KEY_EVENT_ID = "event_id"
    }
}
