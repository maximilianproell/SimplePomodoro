package com.example.simplepomodoro.ui.main

import android.text.format.DateUtils
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplepomodoro.MainActivityViewModel
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme
import com.example.simplepomodoro.R

@Composable
fun SimplePomodoroApp() {
    SimplePomodoroTheme {
        val viewModel: MainActivityViewModel = viewModel()
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
    SimplePomodoroApp()
}