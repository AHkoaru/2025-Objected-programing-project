package com.example.calendar

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.calendar.ui.theme.CalendarAppTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. You can schedule notifications.
            } else {
                // Inform the user that the feature is unavailable because the
                // feature requires a permission that the user has denied.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        askNotificationPermission()

        enableEdgeToEdge()
        setContent {
            CalendarAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalendarApp()
                }
            }
        }
    }
    private fun createNotificationChannel() {
        val name = "Event Reminders" // 채널 이름 (사용자 설정에 보임)
        val descriptionText = "Shows reminders for calendar events" // 채널 설명
        val importance = NotificationManager.IMPORTANCE_HIGH // 알림 중요도
        // 채널 ID는 NotificationReceiver에서 사용하는 ID와 반드시 같아야 합니다.
        val channel = NotificationChannel("EVENT_REMINDERS", name, importance).apply {
            description = descriptionText
        }
        // 시스템에 이 채널을 등록합니다.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
