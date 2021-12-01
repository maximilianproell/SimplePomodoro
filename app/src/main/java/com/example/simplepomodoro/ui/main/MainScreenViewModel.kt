package com.example.simplepomodoro.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.simplepomodoro.Constants.initialTimerSeconds
import com.example.simplepomodoro.ServiceState

class MainScreenViewModel: ViewModel() {
    var mutableServiceState by mutableStateOf(
        ServiceState.STOPPED
    )

    var mutableTimerValueState by mutableStateOf(initialTimerSeconds)
}