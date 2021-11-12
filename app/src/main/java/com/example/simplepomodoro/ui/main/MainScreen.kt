package com.example.simplepomodoro.ui.main

import android.text.format.DateUtils
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplepomodoro.R

@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = viewModel()
    val scaffoldState = rememberScaffoldState()

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

    if (viewModel.timerStateActive) {
        LaunchedEffect(key1 = true) {
            animateScale(targetValue = 1f)
        }
    } else {
        LaunchedEffect(key1 = true) {
            animateScale(targetValue = 0f)
        }
    }

    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                BottomAppBar(
                    cutoutShape = CircleShape
                ) {
                    // Leading icons should typically have a high content alpha
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        IconButton(onClick = { /* doSomething() */ }) {
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
                        if (viewModel.timerStateActive) {
                            // already active: stop timer in that case
                            viewModel.stopPomodoroTimer()
                        } else {
                            // start timer
                            viewModel.startPomodoroTimer()
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
                text = DateUtils.formatElapsedTime(viewModel.timerStateValue),
                modifier = Modifier
                    .fillMaxSize(1f)
                    .wrapContentSize(),
            )
        }
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
    MainScreen()
}