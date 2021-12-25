package com.example.simplepomodoro.di

import android.content.Context
import androidx.room.Room
import com.example.simplepomodoro.data.PomodoroDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(@ApplicationContext applicationContext: Context): PomodoroDatabase {
        return Room.databaseBuilder(
            applicationContext,
            PomodoroDatabase::class.java, "pomodoro-database"
        ).build()

    }
}