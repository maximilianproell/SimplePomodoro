package com.example.simplepomodoro.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.simplepomodoro.data.entities.LabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Insert(onConflict = IGNORE)
    suspend fun insertLabel(label: LabelEntity)

    @Delete
    suspend fun deleteLabel(label: LabelEntity)

    @Query("SELECT * FROM label_table")
    fun getAllLabelsAsFlow(): Flow<List<LabelEntity>>

    @Query("UPDATE LABEL_TABLE SET name = :newName WHERE name = :oldName")
    suspend fun updateLabelName(oldName: String, newName: String)
}