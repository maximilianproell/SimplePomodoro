package com.example.simplepomodoro.ui.components.charts.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

data class ChartDataPoint(
    val xValue: String,
    val yValue: String
)

enum class Axis {
    X_AXIS, Y_AXIS
}

enum class AnimationProgress {
    START, END
}

fun drawAxis(
    drawScope: DrawScope,
    axisColor: Color,
    axisType: Axis
) {
    with(drawScope) {
        val (width, height) = size
        val (start, end) = when (axisType) {
            Axis.X_AXIS -> {
                Offset(0f, height) to Offset(width, height)
            }
            Axis.Y_AXIS -> {
                Offset(0f, 0f) to Offset(0f, height)
            }
        }

        drawLine(
            axisColor,
            start = start,
            end = end,
            strokeWidth = Stroke.DefaultMiter
        )
    }
}