package com.example.simplepomodoro.ui.main

import android.text.format.DateUtils
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplepomodoro.MainActivity
import com.example.simplepomodoro.R
import com.example.simplepomodoro.components.BottomSheetEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MainScreenEvent {
    object OnPauseTimer : MainScreenEvent()
    object OnStartTimer : MainScreenEvent()
    object OnStopTimer : MainScreenEvent()
}

sealed class MainScreenBottomSheetEvent {
    object OnSettingsClick : MainScreenBottomSheetEvent()
    object OnAboutClick : MainScreenBottomSheetEvent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(),
    mainScreenEventHandler: (MainScreenEvent) -> Unit,
    bottomSheetEventHandler: (MainScreenBottomSheetEvent) -> Unit,
    serviceState: MainActivity.ServiceState,
) {
    val scaffoldState = rememberScaffoldState()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = {true}
    )
    val coroutineScope = rememberCoroutineScope()
    val iconScale = remember { Animatable(0f) }

    suspend fun animateScale(targetValue: Float) {
        iconScale.animateTo(
            targetValue = targetValue,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(5f).getInterpolation(it)
                }
            )
        )
    }

    if (serviceState == MainActivity.ServiceState.Running) {
        LaunchedEffect(key1 = true) {
            animateScale(targetValue = 1f)
        }
    } else {
        LaunchedEffect(key1 = true) {
            animateScale(targetValue = 0f)
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            BottomSheetEntry(
                text = stringResource(id = R.string.settings),
                icon = Icons.Filled.Settings,
                onclick = {
                    hideBottomSheet(
                        scope = coroutineScope,
                        bottomSheetState = bottomSheetState,
                        onClickEvent = {
                            // todo find out how to correclty hide that thing
                            bottomSheetEventHandler(
                                MainScreenBottomSheetEvent.OnSettingsClick
                            )
                        })
                }
            )
            BottomSheetEntry(
                text = stringResource(id = R.string.about),
                icon = Icons.Filled.Info,
                onclick = {
                    hideBottomSheet(scope = coroutineScope, bottomSheetState = bottomSheetState) {
                        bottomSheetEventHandler(
                            MainScreenBottomSheetEvent.OnAboutClick
                        )
                    }
                }
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                BottomAppBar(
                    cutoutShape = CircleShape
                ) {
                    // Leading icons should typically have a high content alpha
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                        }
                    }

                    // The actions should be at the end of the BottomAppBar. They use the default medium
                    // content alpha provided by BottomAppBar
                    Spacer(Modifier.weight(1f, true))
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Localized description")
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (serviceState == MainActivity.ServiceState.Running) {
                            // already active: stop timer in that case
                            mainScreenEventHandler(MainScreenEvent.OnStopTimer)
                        } else {
                            // start timer
                            mainScreenEventHandler(MainScreenEvent.OnStartTimer)
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = stringResource(id = R.string.startPomodoroTimer),
                        modifier = Modifier.scale(1 - iconScale.value)
                    )
                    Icon(
                        Icons.Filled.Stop,
                        contentDescription = stringResource(id = R.string.stopPomodoroTimer),
                        modifier = Modifier.scale(iconScale.value)
                    )
                }

            },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
        ) {
            // stateless, as we don't pass the ViewModel
            TimerText(
                text = DateUtils.formatElapsedTime(serviceState.timerValue),
                modifier = Modifier
                    .fillMaxSize(1f)
                    .wrapContentSize(),
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun hideBottomSheet(
    scope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    onClickEvent: () -> Unit) {
    scope.launch {
        bottomSheetState.hide()
        onClickEvent()
    }
}

@Composable
fun TimerText(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = MaterialTheme.typography.h2.fontSize
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen(
        mainScreenEventHandler = {},
        bottomSheetEventHandler = {},
        serviceState = MainActivity.ServiceState.Paused,
    )
}