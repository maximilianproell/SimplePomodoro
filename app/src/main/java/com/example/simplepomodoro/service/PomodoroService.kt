package com.example.simplepomodoro.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.simplepomodoro.Constants
import com.example.simplepomodoro.MainActivity
import com.example.simplepomodoro.R
import com.example.simplepomodoro.ServiceState
import com.example.simplepomodoro.SimplePomodoroApplication.Constants.CHANNEL_ID
import com.example.simplepomodoro.data.DataRepository
import com.example.simplepomodoro.data.entities.SelectedLabel
import com.example.simplepomodoro.data.entities.WorkPackageEntity
import com.example.simplepomodoro.service.PomodoroService.ServiceConstants.timerNotificationId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroService : LifecycleService() {
    @Inject
    lateinit var repository: DataRepository

    private object ServiceConstants {
        const val timerNotificationId = 1
    }

    private val binder = PomodoroServiceBinder()

    private var pomodoroTimer: CountDownTimer? = null
    private var timerValue = Constants.initialTimerSeconds

    private val _pomodoroStateFlow: MutableStateFlow<ServiceState> =
        MutableStateFlow(ServiceState.STOPPED)
    val pomodoroStateFlow: StateFlow<ServiceState> = _pomodoroStateFlow

    private val _timerStateFlow: MutableStateFlow<Long> = MutableStateFlow(timerValue)
    val timerStateFlow: StateFlow<Long> = _timerStateFlow

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Timber.d("on start command called; timerValue now is $timerValue")

        val notification = getMyActivityNotification(timeLeft = timerValue)
        startForeground(timerNotificationId, notification)

        startPomodoroTimer()

        _pomodoroStateFlow.value = ServiceState.RUNNING

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        stopPomodoroTimer()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
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
        // make sure we recreate the timer every time so pausing is possible
        // (we don't want to lose the timer value)
        pomodoroTimer = buildPomodoroTimer()

        pomodoroTimer?.start()
    }

    fun stopPomodoroTimer() {
        pomodoroTimer?.cancel()
        _pomodoroStateFlow.value = ServiceState.STOPPED

        resetTimer()

        stopForeground(true)
    }

    private fun resetTimer() {
        timerValue = Constants.initialTimerSeconds
        _timerStateFlow.value = timerValue
    }

    fun pausePomodoroTimer() {
        pomodoroTimer?.cancel()
        _pomodoroStateFlow.value = ServiceState.PAUSED
    }

    private fun buildPomodoroTimer(): CountDownTimer {
        return object : CountDownTimer(timerValue * 1000, 1000) {
            override fun onTick(p0: Long) {
                timerValue = p0 / 1000
                updateNotification()
                _timerStateFlow.value = timerValue
            }

            override fun onFinish() {
                this@PomodoroService.lifecycleScope.launch(Dispatchers.IO) {
                    val currentlySetLabel = repository.getSelectedLabelSync() ?: SelectedLabel()

                    Timber.d(
                        "inserting finished work package to DB\n" +
                                "seconds worked: ${Constants.initialTimerSeconds}\n" +
                                "label: $currentlySetLabel"
                    )
                    repository.insertWorkPackage(
                        WorkPackageEntity(
                            labelName = currentlySetLabel.selectedLabelName,
                            secondsWorked = Constants.initialTimerSeconds,
                            date = LocalDateTime.now()
                        )
                    )

                    stopPomodoroTimer()
                }
            }
        }
    }
}