package com.example.simplepomodoro

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.simplepomodoro.Constants.initialTimerSeconds
import timber.log.Timber

class MainActivityViewModel: ViewModel() {
    val timerState: MutableState<Long> = mutableStateOf(initialTimerSeconds)

    private val pomodoroTimer: CountDownTimer

    fun startPomodoroTimer(): CountDownTimer = pomodoroTimer.start()

    fun stopPomodoroTimer() = pomodoroTimer.cancel()

    init {
        pomodoroTimer = object : CountDownTimer(initialTimerSeconds * 1000, 1000) {
            override fun onTick(p0: Long) {
                timerState.value = p0 / 1000
            }

            override fun onFinish() {
                Timber.d("pomodoro timer finished")
            }
        }
    }
}