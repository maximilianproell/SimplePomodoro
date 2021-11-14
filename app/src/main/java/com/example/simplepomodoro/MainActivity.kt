package com.example.simplepomodoro

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.simplepomodoro.Constants.initialTimerSeconds
import com.example.simplepomodoro.navigation.PomodoroNavHost
import com.example.simplepomodoro.service.PomodoroService
import com.example.simplepomodoro.ui.main.MainScreenEvent
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme
import timber.log.Timber

object Constants {
    const val initialTimerSeconds: Long = 1500
}

class MainActivity : ComponentActivity() {
    sealed class ServiceState {
        object Running: ServiceState()
        object Paused: ServiceState()
        object Stopped: ServiceState()
        val timerValue: Long = initialTimerSeconds
    }

    private var pomodoroService: PomodoroService? = null
    private var mutableServiceState by mutableStateOf<ServiceState>(
        ServiceState.Stopped
    )

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as PomodoroService.PomodoroServiceBinder
            pomodoroService = binder.getService()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            pomodoroService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroApp(mainScreenEventHandler = { event ->
                when (event) {
                    MainScreenEvent.OnPauseTimer -> {

                    }
                    MainScreenEvent.OnStartTimer -> {
                        startService(
                            Intent(this, PomodoroService::class.java)
                        )
                        mutableServiceState = ServiceState.Running
                    }
                    MainScreenEvent.OnStopTimer -> {
                        stopService(
                            Intent(this, PomodoroService::class.java)
                        )
                        unbindService(connection)
                        mutableServiceState = ServiceState.Stopped
                    }
                }
            },
            serviceState = mutableServiceState)
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
        pomodoroService = null
    }
}

@Composable
fun PomodoroApp(
    mainScreenEventHandler: (MainScreenEvent) -> Unit,
    serviceState: MainActivity.ServiceState
) {
    SimplePomodoroTheme {
        val navController = rememberNavController()
        PomodoroNavHost(
            navController = navController,
            serviceState = serviceState,
            mainScreenEventHandler = mainScreenEventHandler,
        )
    }
}
