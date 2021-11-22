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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.simplepomodoro.Constants.initialTimerSeconds
import com.example.simplepomodoro.navigation.PomodoroNavHost
import com.example.simplepomodoro.service.PomodoroService
import com.example.simplepomodoro.ui.main.MainScreenEvent
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
             pomodoroService?.pomodoroState?.let { serviceStateFlow ->
                 lifecycleScope.launch {
                     repeatOnLifecycle(Lifecycle.State.STARTED) {
                         serviceStateFlow.collect { state ->
                             mutableServiceState = state
                         }
                     }
                 }
            }
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
                        bindPomodoroService()
                    }
                    MainScreenEvent.OnStopTimer -> {
                        stopService(
                            Intent(this, PomodoroService::class.java)
                        )
                        unbindService(connection)
                    }
                }
            },
            serviceState = mutableServiceState)
        }
    }

    private fun bindPomodoroService() {
        Intent(this, PomodoroService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStart() {
        super.onStart()
        bindPomodoroService()
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
