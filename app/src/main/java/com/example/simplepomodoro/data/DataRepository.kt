package com.example.simplepomodoro.data

import com.example.simplepomodoro.data.entities.LabelEntity
import com.example.simplepomodoro.data.entities.SelectedLabel
import com.example.simplepomodoro.data.entities.WorkPackageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val database: PomodoroDatabase
) {
    suspend fun insertWorkPackage(workPackageEntity: WorkPackageEntity) {
        database.workPackageDao().insertWorkPackage(workPackageEntity)
    }

    fun getAllLabelsAsFlow() = database
        .labelDao()
        .getAllLabelsAsFlow()
        .flowOn(Dispatchers.IO)

    suspend fun insertLabel(label: LabelEntity) = database.labelDao().insertLabel(label)

    suspend fun deleteLabel(label: LabelEntity) = database.labelDao().deleteLabel(label)

    suspend fun updateLabelName(oldName: String, newName: String) =
        database.labelDao().updateLabelName(oldName, newName)

    fun getSelectedLabelSync() = database.selectedLabelDao().getSelectedLabelSync()

    fun getSelectedLabelFlow() =
        database
            .selectedLabelDao()
            .getSelectedLabelFlow()
            .map {
                // there is no selected label yet
                it ?: SelectedLabel()
            }
            .flowOn(Dispatchers.IO)

    suspend fun selectLabel(selectedLabel: SelectedLabel) =
        database.selectedLabelDao().selectLabel(selectedLabel)

    fun getAllWorkPackagesAsFlow() =
        database
            .workPackageDao()
            .getAllWorkPackagesAsFlow()
            .flowOn(Dispatchers.IO)
}