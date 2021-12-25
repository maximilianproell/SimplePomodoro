package com.example.simplepomodoro.data

import com.example.simplepomodoro.data.entities.WorkPackageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
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
}