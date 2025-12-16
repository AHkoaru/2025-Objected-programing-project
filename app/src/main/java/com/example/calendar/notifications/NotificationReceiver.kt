package com.example.calendar.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.calendar.R

const val EXTRA_EVENT_ID = "event_id"
const val EXTRA_EVENT_TITLE = "event_title"
const val EXTRA_EVENT_DESCRIPTION = "event_description"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmCheck", "NotificationRec")
        val title = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Event Reminder"
        val description = intent.getStringExtra(EXTRA_EVENT_DESCRIPTION) ?: "You have an upcoming event."
        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "EVENT_REMINDERS")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: Replace with a suitable icon
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(eventId?.hashCode() ?: System.currentTimeMillis().toInt(), notification)
    }
}
