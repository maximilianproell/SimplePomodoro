package com.example.simplepomodoro.navigation

sealed class PomodoroScreen(val routeName: String) {
    object Main: PomodoroScreen("main_screen")
    object Settings: PomodoroScreen("settings_screen")
    object Statistics: PomodoroScreen("statistics_screen")
    object Labels: PomodoroScreen("labels_screen")
}