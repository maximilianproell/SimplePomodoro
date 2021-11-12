package com.example.simplepomodoro.ui.main

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.simplepomodoro.Constants.initialTimerSeconds

class MainScreenViewModel: ViewModel() {
    var timerStateValue by mutableStateOf(initialTimerSeconds)
        private set

    var timerStateActive by mutableStateOf(false)
        private set

    private val pomodoroTimer: CountDownTimer

    fun startPomodoroTimer() {
        pomodoroTimer.start()
        timerStateActive = true
    }

    fun stopPomodoroTimer() {
        pomodoroTimer.cancel()
        timerStateActive = false
        timerStateValue = initialTimerSeconds
    }

    init {
        pomodoroTimer = object : CountDownTimer(initialTimerSeconds * 1000, 1000) {
            override fun onTick(p0: Long) {
                timerStateValue = p0 / 1000
            }

            override fun onFinish() {
                timerStateActive = false
            }
        }
    }
}