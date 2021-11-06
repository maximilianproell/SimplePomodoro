package com.example.simplepomodoro.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import timber.log.Timber

class PomodoroService: Service() {
    private val binder = PomodoroServiceBinder()

    override fun onCreate() {
        super.onCreate()
        Timber.d("on create called")
    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.d("on bind called")
        return binder
    }

    inner class PomodoroServiceBinder: Binder() {
        fun getService(): PomodoroService = this@PomodoroService
    }
}