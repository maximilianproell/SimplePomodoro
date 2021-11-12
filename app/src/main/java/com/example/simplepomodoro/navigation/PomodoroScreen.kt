package com.example.simplepomodoro.navigation

sealed class PomodoroScreen(val routeName: String) {
    object Main: PomodoroScreen("main_screen")
    object Settings: PomodoroScreen("settings_screen")
}