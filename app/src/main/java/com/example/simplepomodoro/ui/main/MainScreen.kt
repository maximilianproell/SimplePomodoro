package com.example.simplepomodoro.ui.main

import android.content.Context
import android.text.format.DateUtils
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.simplepomodoro.Constants
import com.example.simplepomodoro.Constants.currentLabelSharedPref
import com.example.simplepomodoro.Constants.sharedPrefIdentifier
import com.example.simplepomodoro.R
import com.example.simplepomodoro.ServiceState
import com.example.simplepomodoro.ui.components.BottomSheetEntry
import com.example.simplepomodoro.ui.components.Chip
import com.example.simplepomodoro.ui.components.PomodoroDialog
import com.example.simplepomodoro.data.entities.LabelEntity
import com.example.simplepomodoro.navigation.PomodoroScreen
import com.example.simplepomodoro.utils.convertLabelNameToDisplayName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MainScreenEvent {
    object OnPauseTimer : MainScreenEvent()
    object OnStartTimer : MainScreenEvent()
    object OnStopTimer : MainScreenEvent()
}

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    mainScreenEventHandler: (MainScreenEvent) -> Unit,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { true }
    )
    val coroutineScope = rememberCoroutineScope()

    var miniFabExpandedState by remember { mutableStateOf(false) }
    var showLabelDialog by remember { mutableStateOf(false) }

    val sharedPref = LocalContext.current.getSharedPreferences(
        sharedPrefIdentifier, Context.MODE_PRIVATE
    )

    var currentlySetLabel by remember {
        mutableStateOf(sharedPref.getString(currentLabelSharedPref, Constants.noLabelLabel)!!)
    }

    val allLabels by viewModel.allLabelsFlow.collectAsState(initial = emptyList())

    val mainFabIconScale = animateFloatAsState(
        targetValue = if (viewModel.mutableServiceState == ServiceState.RUNNING) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = {
                OvershootInterpolator(5f).getInterpolation(it)
            }
        )
    )

    val timerOpacityAnimation by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = if (viewModel.mutableServiceState == ServiceState.PAUSED) .4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                            modifier = Modifier.scale(1 - mainFabIconScale.value)
                        )

                        if (miniFabExpandedState) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(id = R.string.stopPomodoroTimer),
                                modifier = Modifier.scale(mainFabIconScale.value)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Stop,
                                contentDescription = stringResource(id = R.string.stopPomodoroTimer),
                                modifier = Modifier.scale(mainFabIconScale.value)
                            )
                        }

                    }
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TimerAndReset(
                        timerOpacityAnimation = timerOpacityAnimation,
                        isResetVisible = viewModel.mutableServiceState == ServiceState.PAUSED,
                        onResetIconClick = { mainScreenEventHandler(MainScreenEvent.OnStopTimer) },
                        timerSecondsLeft = viewModel.mutableTimerValueState
                    )

                    Chip(
                        name = convertLabelNameToDisplayName(
                            labelName = currentlySetLabel,
                            noLabelName = stringResource(id = R.string.no_label)
                        ),
                        onSelectionChanged = {
                            showLabelDialog = true
                        },
                    )
                    LabelDialog(
                        showDialog = showLabelDialog,
                        onDismiss = { showLabelDialog = false },
                        labels = allLabels,
                        currentlySetLabel = currentlySetLabel,
                        onLabelChanged = { newLabel ->
                            currentlySetLabel = newLabel
                            sharedPref
                                .edit()
                                .putString(currentLabelSharedPref, newLabel)
                                .apply()
                        },
                        onEditLabelsClicked = {
                            navController.navigate(PomodoroScreen.Labels.routeName)
                        }
                    )
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

@Composable
private fun TimerAndReset(
    timerOpacityAnimation: Float,
    isResetVisible: Boolean,
    onResetIconClick: () -> Unit,
    timerSecondsLeft: Long
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .alpha(timerOpacityAnimation),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimerText(
            text = DateUtils.formatElapsedTime(timerSecondsLeft),
            modifier = Modifier
        )

        ResetIcon(
            modifier = Modifier.size(56.dp),
            isVisible = isResetVisible,
            onResetIconClick = onResetIconClick
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
        fontSize = MaterialTheme.typography.h1.fontSize
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ResetIcon(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    onResetIconClick: () -> Unit
) {
    AnimatedVisibility(visible = isVisible) {
        IconButton(
            onClick = { onResetIconClick() },
            modifier = modifier
        ) {
            Icon(
                modifier = modifier,
                imageVector = Icons.Filled.Replay,
                contentDescription = "Reset timer"
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LabelDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    labels: List<LabelEntity>,
    currentlySetLabel: String,
    onLabelChanged: (String) -> Unit = {},
    onEditLabelsClicked: () -> Unit = {}
) {
    var temporarilySetLabel by remember { mutableStateOf(currentlySetLabel) }

    PomodoroDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.chose_label),
                    style = MaterialTheme.typography.h6
                )

                IconButton(onClick = {
                    onEditLabelsClicked()
                    onDismiss()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = stringResource(id = R.string.edit_labels)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(labels) { label ->
                    Chip(
                        name = label.name,
                        isSelected = label.name == temporarilySetLabel,
                        onSelectionChanged = {
                            temporarilySetLabel = it
                        }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onDismiss() }) {
                    Text(
                        stringResource(id = R.string.cancel),
                        color = MaterialTheme.colors.onBackground
                    )
                }
                TextButton(onClick = {
                    onLabelChanged(temporarilySetLabel)
                    onDismiss()
                }) {
                    Text(
                        "OK",
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
        }
    }
}