package com.example.simplepomodoro.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.simplepomodoro.Constants
import com.example.simplepomodoro.MainActivity
import com.example.simplepomodoro.R
import com.example.simplepomodoro.SimplePomodoroApplication.Constants.CHANNEL_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import android.app.Notification
import android.app.NotificationManager

import android.app.PendingIntent
import android.content.Context
import android.text.format.DateUtils
import com.example.simplepomodoro.service.PomodoroService.ServiceConstants.timerNotificationId

class PomodoroService : Service() {
    private object ServiceConstants {
        const val timerNotificationId = 1
    }

    private val binder = PomodoroServiceBinder()

    private val pomodoroTimer: CountDownTimer
    private var isTimerActive = false
    private var timerValue = Constants.initialTimerSeconds

    private val _pomodoroState: MutableStateFlow<MainActivity.ServiceState> =
        MutableStateFlow(MainActivity.ServiceState.Stopped)

    val pomodoroState: StateFlow<MainActivity.ServiceState> = _pomodoroState

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notification = getMyActivityNotification(timeLeft = timerValue)
        startForeground(timerNotificationId, notification)

        startPomodoroTimer()

        _pomodoroState.value = MainActivity.ServiceState.Running

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        stopPomodoroTimer()
        _pomodoroState.value = MainActivity.ServiceState.Stopped
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class PomodoroServiceBinder : Binder() {
        fun getService(): PomodoroService = this@PomodoroService
    }

    private fun getMyActivityNotification(timeLeft: Long): Notification {
        // The PendingIntent to launch our activity if the user selects
        // this notification
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getText(R.string.app_name))
            .setContentText("Time left: ${DateUtils.formatElapsedTime(timeLeft)}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .build()
    }

    private fun updateNotification() {
        val notification = getMyActivityNotification(timeLeft = timerValue)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            notify(timerNotificationId, notification)
        }
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
                updateNotification()
            }

            override fun onFinish() {
                isTimerActive = false
            }
        }
    }
}