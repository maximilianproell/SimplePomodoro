package com.example.simplepomodoro.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.simplepomodoro.Constants
import com.example.simplepomodoro.R
import com.example.simplepomodoro.SimplePomodoroApplication.Constants.CHANNEL_ID
import timber.log.Timber

class PomodoroService: Service() {
    private val binder = PomodoroServiceBinder()

    private val pomodoroTimer: CountDownTimer
    private var isTimerActive = false
    private var timerValue = Constants.initialTimerSeconds

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getText(R.string.app_name))
            .setContentTitle("This is a test service now")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)

        startPomodoroTimer()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        stopPomodoroTimer()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class PomodoroServiceBinder: Binder() {
        fun getService(): PomodoroService = this@PomodoroService
    }

    private fun startPomodoroTimer() {
        pomodoroTimer.start()
        isTimerActive = true
    }

    private fun stopPomodoroTimer() {
        pomodoroTimer.cancel()
        isTimerActive = false
    }

    init {
        pomodoroTimer = object : CountDownTimer(Constants.initialTimerSeconds * 1000, 1000) {
            override fun onTick(p0: Long) {
                timerValue = p0 / 1000
            }

            override fun onFinish() {
                isTimerActive = false
            }
        }
    }
}