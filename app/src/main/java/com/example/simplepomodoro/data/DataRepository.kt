package com.example.simplepomodoro.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val database: PomodoroDatabase
) {

}