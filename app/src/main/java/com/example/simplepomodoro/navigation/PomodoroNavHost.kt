package com.example.simplepomodoro.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.simplepomodoro.ui.labels.LabelManagerScreen
import com.example.simplepomodoro.ui.statistics.StatisticsScreen
import com.example.simplepomodoro.ui.main.MainScreen
import com.example.simplepomodoro.ui.main.MainScreenEvent
import com.example.simplepomodoro.ui.settings.SettingsScreen

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PomodoroNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainScreenEventHandler: (MainScreenEvent) -> Unit,
) {
    val mainActivityViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    NavHost(
        navController = navController,
        startDestination = PomodoroScreen.Main.routeName,
        modifier = modifier
    ) {
        composable(route = PomodoroScreen.Main.routeName) {
            MainScreen(
                // here we directly pass the viewModelStoreOwner so we get the same viewModel as
                // in the MainActivity
                // using CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner)
                // instead would also be possible here
                viewModel = hiltViewModel(viewModelStoreOwner = mainActivityViewModelStoreOwner),
                mainScreenEventHandler = mainScreenEventHandler,
                navController = navController,
            )
        }

        composable(route = PomodoroScreen.Settings.routeName) {
            SettingsScreen()
        }

        composable(route = PomodoroScreen.Statistics.routeName) {
            StatisticsScreen()
        }

        composable(route = PomodoroScreen.Labels.routeName) {
            LabelManagerScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
    }
}