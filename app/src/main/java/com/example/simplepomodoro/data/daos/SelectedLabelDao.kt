package com.example.simplepomodoro.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.simplepomodoro.data.entities.SelectedLabel
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedLabelDao {
    @Insert(onConflict = REPLACE)
    suspend fun selectLabel(selectedLabel: SelectedLabel)

    @Query("SELECT * FROM selectedlabel LIMIT 1")
    fun getSelectedLabelFlow(): Flow<SelectedLabel?>
}