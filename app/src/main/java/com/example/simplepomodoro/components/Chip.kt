package com.example.simplepomodoro.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplepomodoro.R
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme
import com.example.simplepomodoro.utils.convertLabelNameToDisplayName

@Composable
@Preview
fun ChipPreview() {
    SimplePomodoroTheme {
        var isSelected by remember { mutableStateOf(true) }
        Chip(
            isSelected = isSelected,
            onSelectionChanged = { isSelected = !isSelected}
        )
    }
}

@kotlin.OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun Chip(
    name: String = "Chip",
    isSelected: Boolean = false,
    onSelectionChanged: (String) -> Unit = {},
) {
    Surface(
        elevation = 8.dp,
        shape = RoundedCornerShape(80),
        color = MaterialTheme.colors.primary,
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = {
                    onSelectionChanged(name)
                }
            )
            .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = isSelected,
                enter = expandIn(expandFrom = Alignment.Center),
                exit = shrinkOut(shrinkTowards = Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircleOutline,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                text = convertLabelNameToDisplayName(
                    labelName = name,
                    noLabelName = stringResource(id = R.string.no_label),
                ),
                style = MaterialTheme.typography.body2,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}