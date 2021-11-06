package com.example.simplepomodoro

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.simplepomodoro.service.PomodoroService
import com.example.simplepomodoro.ui.main.SimplePomodoroApp
import timber.log.Timber

object Constants {
    const val initialTimerSeconds: Long = 5
}

class MainActivity : ComponentActivity() {
    private lateinit var pomodoroService: PomodoroService
    private var isServiceBound = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as PomodoroService.PomodoroServiceBinder
            pomodoroService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isServiceBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimplePomodoroApp()
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("start state called of activity")
        Intent(this, PomodoroService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindPomodoroService()
    }

    private fun unbindPomodoroService() {
        unbindService(connection)
        isServiceBound = false
    }
}

