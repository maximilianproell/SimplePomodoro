package com.example.simplepomodoro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.simplepomodoro.SimplePomodoroApplication.Constants.CHANNEL_ID
import com.example.simplepomodoro.SimplePomodoroApplication.Constants.SERVICE_CHANNEL_NAME
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class SimplePomodoroApplication: Application() {
    object Constants {
        const val CHANNEL_ID = "simple_pomodoro_service_id"
        const val SERVICE_CHANNEL_NAME = "Pomodoro Timer Service Channel"
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            // we could use a different crash handling method, like generating crash log
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                SERVICE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).apply {
                createNotificationChannel(serviceChannel)
            }
        }
    }
}