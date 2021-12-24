package com.example.simplepomodoro.ui.main

import android.text.format.DateUtils
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simplepomodoro.R
import com.example.simplepomodoro.ServiceState
import com.example.simplepomodoro.components.BottomSheetEntry
import com.example.simplepomodoro.components.Chip
import com.example.simplepomodoro.navigation.PomodoroScreen
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

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(),
    mainScreenEventHandler: (MainScreenEvent) -> Unit,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { true }
    )
    val coroutineScope = rememberCoroutineScope()
    val iconScale = remember { Animatable(0f) }

    var miniFabExpandedState by remember { mutableStateOf(false) }

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

    if (viewModel.mutableServiceState == ServiceState.RUNNING) {
        LaunchedEffect(key1 = true) {
            animateScale(targetValue = 1f)
        }
    } else {
        LaunchedEffect(key1 = true) {
            animateScale(targetValue = 0f)
        }
    }

    val constraints = ConstraintSet {
        val miniFabs = createRefFor("mini_fabs")
        constrain(miniFabs) {
            bottom.linkTo(parent.bottom, margin = 90.dp)

            width = Dimension.wrapContent
            height = Dimension.wrapContent

            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }

    ConstraintLayout(constraintSet = constraints, modifier = Modifier.fillMaxSize()) {
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
                                // todo find out how to correctly hide that thing
                                navController.navigate(PomodoroScreen.Settings.routeName)
                            })
                    }
                )
                BottomSheetEntry(
                    text = stringResource(id = R.string.about),
                    icon = Icons.Filled.Info,
                    onclick = {
                        hideBottomSheet(
                            scope = coroutineScope,
                            bottomSheetState = bottomSheetState
                        ) {
                            // todo: navigate to about screen
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
                                Icon(
                                    Icons.Filled.Menu,
                                    contentDescription = "Localized description"
                                )
                            }
                        }

                        // The actions should be at the end of the BottomAppBar. They use the default medium
                        // content alpha provided by BottomAppBar
                        Spacer(Modifier.weight(1f, true))
                        IconButton(onClick = {
                            navController.navigate(PomodoroScreen.Statistics.routeName)
                        }) {
                            Icon(
                                Icons.Filled.TrendingUp,
                                contentDescription = "Statistics"
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier
                            .layoutId("main_fab")
                            .wrapContentSize(),
                        onClick = {
                            if (viewModel.mutableServiceState == ServiceState.RUNNING) {
                                miniFabExpandedState = !miniFabExpandedState
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

                        // todo this could have its own animation

                        if (miniFabExpandedState) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(id = R.string.stopPomodoroTimer),
                                modifier = Modifier.scale(iconScale.value)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Stop,
                                contentDescription = stringResource(id = R.string.stopPomodoroTimer),
                                modifier = Modifier.scale(iconScale.value)
                            )
                        }

                    }
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TimerText(
                        text = DateUtils.formatElapsedTime(viewModel.mutableTimerValueState),
                        modifier = Modifier
                            .wrapContentSize(),
                    )

                    Chip(name = stringResource(id = R.string.no_label))
                }
            }
        }
        PomodoroMiniFabs(
            expandedState = miniFabExpandedState,
            onStopClick = {
                mainScreenEventHandler(MainScreenEvent.OnStopTimer)
                miniFabExpandedState = false
            },
            onPauseClick = {
                mainScreenEventHandler(MainScreenEvent.OnPauseTimer)
                miniFabExpandedState = false
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PomodoroMiniFabs(
    expandedState: Boolean,
    onStopClick: () -> Unit,
    onPauseClick: () -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier
            .wrapContentSize()
            .layoutId("mini_fabs"),
        visible = expandedState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FloatingActionButton(
                modifier = Modifier.size(56.dp),
                onClick = {
                    onPauseClick()
                }
            ) {
                Icon(
                    Icons.Filled.Pause,
                    contentDescription = stringResource(id = R.string.startPomodoroTimer),
                )
            }

            FloatingActionButton(
                onClick = {
                    onStopClick()
                }
            ) {
                Icon(
                    Icons.Filled.Stop,
                    contentDescription = stringResource(id = R.string.startPomodoroTimer),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun hideBottomSheet(
    scope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    onClickEvent: () -> Unit
) {
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