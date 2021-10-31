package com.example.simplepomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme

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
                    FloatingActionButton(onClick = {
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Localized description")
                    }
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center
            ) {
                Greeting(
                    name = "Android",
                    modifier = Modifier
                        .then(
                            Modifier
                                .fillMaxSize(1f)
                                .wrapContentSize()
                        )
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimplePomodoroApp()
}