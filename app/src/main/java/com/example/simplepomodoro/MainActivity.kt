package com.example.simplepomodoro

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.simplepomodoro.navigation.PomodoroNavHost
import com.example.simplepomodoro.service.PomodoroService
import com.example.simplepomodoro.ui.main.MainScreenEvent
import com.example.simplepomodoro.ui.main.MainScreenViewModel
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme
import com.example.simplepomodoro.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

object Constants {
    const val initialTimerSeconds: Long = 1500
}

enum class ServiceState {
    RUNNING, PAUSED, STOPPED
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var pomodoroService: PomodoroService? = null
    private val viewModel: MainScreenViewModel by viewModels()


    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as PomodoroService.PomodoroServiceBinder
            pomodoroService = binder.getService()
            pomodoroService?.pomodoroStateFlow?.let { serviceStateFlow: Flow<ServiceState> ->
                serviceStateFlow.launchAndCollectIn(this@MainActivity) { state ->
                    Timber.i("state now is: $state")
                    viewModel.mutableServiceState = state
                }
            }

            pomodoroService?.timerStateFlow?.let { timerStateFlow ->
                timerStateFlow.launchAndCollectIn(this@MainActivity) {
                    viewModel.mutableTimerValueState = it
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
            PomodoroApp(
                mainScreenEventHandler = { event ->
                    when (event) {
                        MainScreenEvent.OnPauseTimer -> {
                            pomodoroService?.pausePomodoroTimer()
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
                            unbindPomodoroService()
                            viewModel.resetTimerState()
                        }
                    }
                }
            )
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
        if (pomodoroService != null) {
            unbindService(connection)
            pomodoroService = null
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PomodoroApp(
    mainScreenEventHandler: (MainScreenEvent) -> Unit,
) {
    SimplePomodoroTheme {
        val navController = rememberNavController()
        PomodoroNavHost(
            navController = navController,
            mainScreenEventHandler = mainScreenEventHandler,
        )
    }
}
