package com.example.simplepomodoro.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.simplepomodoro.data.entities.WorkPackageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkPackageDao {
    @Insert
    fun insertWorkPackage(workPackageEntity: WorkPackageEntity)

    @Query("SELECT * FROM work_package_table WHERE labelName = :labelName")
    fun getAllWorkPackagesWithLabelAsFlow(labelName: String): Flow<List<WorkPackageEntity>>

    @Query("SELECT * FROM work_package_table")
    fun getAllWorkPackagesAsFlow(): Flow<List<WorkPackageEntity>>
}