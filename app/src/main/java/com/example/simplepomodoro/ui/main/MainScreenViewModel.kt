package com.example.simplepomodoro.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplepomodoro.Constants.initialTimerSeconds
import com.example.simplepomodoro.ServiceState
import com.example.simplepomodoro.data.DataRepository
import com.example.simplepomodoro.data.entities.SelectedLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
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

    val selectedLabelFlow = repository.getSelectedLabelFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SelectedLabel()
    )

    fun selectLabel(selectedLabel: SelectedLabel) = viewModelScope.launch(Dispatchers.IO) {
        repository.selectLabel(selectedLabel = selectedLabel)
    }
}