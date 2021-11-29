package com.example.simplepomodoro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    var mutableServiceState by mutableStateOf<MainActivity.ServiceState>(
        MainActivity.ServiceState.Stopped
    )
}