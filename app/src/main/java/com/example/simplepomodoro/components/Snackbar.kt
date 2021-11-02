package com.example.simplepomodoro.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simplepomodoro.ui.theme.LightOrange

@Composable
fun DismissableSnackbar(scaffoldState: ScaffoldState, snackbarMessage: String) {
    Snackbar(
        action = {
            TextButton(
                onClick = {
                    scaffoldState
                        .snackbarHostState
                        .currentSnackbarData
                        ?.dismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = LightOrange
                )
            ) {
                Text("Dismiss")
            }
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Text(snackbarMessage)
    }
}