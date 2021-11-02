package com.example.simplepomodoro

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class SimplePomodoroApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            // we could use a different crash handling method, like generating crash log
        }
    }
}