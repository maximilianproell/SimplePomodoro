package com.example.simplepomodoro.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetEntry(
    text: String,
    icon: ImageVector,
    onclick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onclick()
            }
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text
        )
        Text(text,
            style = MaterialTheme.typography.button
        )
    }
}

@Composable
@Preview
fun BottomSheetEntryPreview() {
    BottomSheetEntry(text = "Settings", icon = Icons.Filled.Settings) {

    }
}