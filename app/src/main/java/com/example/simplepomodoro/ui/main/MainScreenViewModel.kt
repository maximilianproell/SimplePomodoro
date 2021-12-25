package com.example.simplepomodoro.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplepomodoro.Constants.initialTimerSeconds
import com.example.simplepomodoro.ServiceState
import com.example.simplepomodoro.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    repository: DataRepository
): ViewModel() {
    var mutableServiceState by mutableStateOf(
        ServiceState.STOPPED
    )

    var mutableTimerValueState by mutableStateOf(initialTimerSeconds)

    fun resetTimerState() {
        mutableTimerValueState = initialTimerSeconds
    }

    val allLabelsFlow = repository.getAllLabelsAsFlow().shareIn(
        scope = viewModelScope,
        replay = 1,
        started = SharingStarted.Eagerly
    )
}