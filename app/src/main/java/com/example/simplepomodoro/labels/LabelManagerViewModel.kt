package com.example.simplepomodoro.labels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplepomodoro.data.DataRepository
import com.example.simplepomodoro.data.entities.LabelEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelManagerViewModel @Inject constructor(
    val repository: DataRepository
): ViewModel() {
    fun insertLabel(label: LabelEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertLabel(label)
    }
}