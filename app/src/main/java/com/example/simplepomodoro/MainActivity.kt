package com.example.simplepomodoro

import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme

object Constants {
    const val initialTimerSeconds: Long = 30
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimplePomodoroApp()
        }
    }
}

@Composable
fun SimplePomodoroApp() {
    SimplePomodoroTheme {
        val viewModel: MainActivityViewModel = viewModel()
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Scaffold(
                bottomBar = {
                    BottomAppBar(
                        cutoutShape = CircleShape
                    ) {

                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            viewModel.startPomodoroTimer()
                        }
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = stringResource(id = R.string.startPomodoroTimer)
                        )
                    }

                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center
            ) {
                TimerText(
                    viewModel = viewModel,
                    modifier = Modifier
                        .then(
                            Modifier
                                .fillMaxSize(1f)
                                .wrapContentSize()
                        ),
                )
            }
        }
    }
}

@Composable
fun TimerText(viewModel: MainActivityViewModel, modifier: Modifier) {
    Text(
        text = DateUtils.formatElapsedTime(viewModel.timerState.value),
        modifier = modifier,
        fontSize = MaterialTheme.typography.h2.fontSize
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimplePomodoroApp()
}