package com.example.simplepomodoro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.simplepomodoro.ui.main.MainScreen

@Composable
fun PomodoroNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = PomodoroScreen.Main.routeName,
        modifier = modifier
    ) {
        composable(route = PomodoroScreen.Main.routeName) {
            MainScreen()
        }
    }
}