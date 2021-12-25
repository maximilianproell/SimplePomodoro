package com.example.simplepomodoro.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplepomodoro.data.entities.LabelEntity
import com.example.simplepomodoro.data.entities.WorkPackageEntity

@Database(entities = [LabelEntity::class, WorkPackageEntity::class], version = 1)
abstract class PomodoroDatabase: RoomDatabase() {
}