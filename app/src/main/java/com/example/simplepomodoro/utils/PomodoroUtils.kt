package com.example.simplepomodoro.utils

import androidx.compose.ui.graphics.Color
import com.example.simplepomodoro.Constants

/**
 * This helper function converts the given label name to the right name, which should be displayed.
 * It only converts the "no-label" String to the given noLabelName. This is useful when different
 * languages are used.
 */
fun convertLabelNameToDisplayName(labelName: String, noLabelName: String): String {
    return if (labelName == Constants.noLabelLabel) noLabelName
    else labelName
}

fun Color.toLegacyInt(): Int {
    return android.graphics.Color.argb(
        (alpha * 255F + .5F).toInt(),
        (red * 255F + .5F).toInt(),
        (green * 255F + .5F).toInt(),
        (blue * 255F + .5F).toInt()
    )
}