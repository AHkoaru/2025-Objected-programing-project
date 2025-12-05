package com.example.calendar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class CalendarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Event Reminders"
            val descriptionText = "Notifications for upcoming events"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("EVENT_REMINDERS", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
