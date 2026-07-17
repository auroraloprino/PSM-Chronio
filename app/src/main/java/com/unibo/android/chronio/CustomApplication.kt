package com.unibo.android.chronio

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.unibo.android.data.di.RepositoryProviderImpl
import com.unibo.android.domain.di.UseCasesProvider

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UseCasesProvider.setup(RepositoryProviderImpl(this))
        UseCasesProvider.reminderScheduler = ReminderSchedulerImpl(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            ReminderWorker.CHANNEL_ID,
            "Promemoria eventi",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Notifiche di promemoria per gli eventi del calendario" }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
