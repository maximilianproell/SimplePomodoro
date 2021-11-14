package com.example.simplepomodoro.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.simplepomodoro.MainActivity
import com.example.simplepomodoro.ui.main.MainScreen
import com.example.simplepomodoro.ui.main.MainScreenEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PomodoroNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    serviceState: MainActivity.ServiceState,
    mainScreenEventHandler: (MainScreenEvent) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = PomodoroScreen.Main.routeName,
        modifier = modifier
    ) {
        composable(route = PomodoroScreen.Main.routeName) {
            MainScreen(
                mainScreenEventHandler = mainScreenEventHandler,
                serviceState = serviceState
            )
        }
    }
}