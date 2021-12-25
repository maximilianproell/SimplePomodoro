package com.example.simplepomodoro.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.simplepomodoro.data.daos.LabelDao
import com.example.simplepomodoro.data.daos.WorkPackageDao
import com.example.simplepomodoro.data.entities.LabelEntity
import com.example.simplepomodoro.data.entities.WorkPackageEntity

@Database(entities = [LabelEntity::class, WorkPackageEntity::class], version = 1)
@TypeConverters(com.example.simplepomodoro.data.TypeConverters::class)
abstract class PomodoroDatabase: RoomDatabase() {
    abstract fun labelDao(): LabelDao
    abstract fun workPackageDao(): WorkPackageDao
}