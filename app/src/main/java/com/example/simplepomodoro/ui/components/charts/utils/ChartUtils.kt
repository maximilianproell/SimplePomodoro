package com.example.simplepomodoro.ui.components.charts.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.example.simplepomodoro.utils.toLegacyInt

data class ChartDataPoint(
    val xValue: String,
    val yValue: Float
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
    axisType: Axis,
    offsetPadding: Float = 0f
) {
    with(drawScope) {
        val (width, height) = size
        val (start, end) = when (axisType) {
            Axis.X_AXIS -> {
                Offset(offsetPadding, height - offsetPadding) to Offset(width, height - offsetPadding)
            }
            Axis.Y_AXIS -> {
                Offset(offsetPadding, 0f) to Offset(offsetPadding, height - offsetPadding)
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

fun drawYAxisLabel(
    drawScope: DrawScope,
    labelColor: Color,
    labelTextSizePx: Float
) {
    drawScope.drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawText(
            "halloo",
            100f, 100f,
            android.graphics.Paint().apply {
                textSize = labelTextSizePx
                color = labelColor.toLegacyInt()
            }
        )
    }
}