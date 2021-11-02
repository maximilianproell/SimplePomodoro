package com.example.simplepomodoro

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.simplepomodoro.Constants.initialTimerSeconds
import timber.log.Timber

class MainActivityViewModel: ViewModel() {
    var timerStateValue by mutableStateOf(initialTimerSeconds)
        private set

    private val pomodoroTimer: CountDownTimer

    fun startPomodoroTimer(): CountDownTimer = pomodoroTimer.start()

    fun stopPomodoroTimer() = pomodoroTimer.cancel()

    init {
        pomodoroTimer = object : CountDownTimer(initialTimerSeconds * 1000, 1000) {
            override fun onTick(p0: Long) {
                timerStateValue = p0 / 1000
            }

            override fun onFinish() {
                Timber.d("pomodoro timer finished")
            }
        }
    }
}