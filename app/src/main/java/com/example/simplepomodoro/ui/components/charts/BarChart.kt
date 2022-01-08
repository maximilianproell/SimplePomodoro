package com.example.simplepomodoro.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplepomodoro.ui.components.charts.utils.AnimationProgress
import com.example.simplepomodoro.ui.components.charts.utils.Axis
import com.example.simplepomodoro.ui.components.charts.utils.ChartDataPoint
import com.example.simplepomodoro.ui.components.charts.utils.drawAxis
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme
import com.example.simplepomodoro.utils.toLegacyInt

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    dataPoints: List<ChartDataPoint>,
    barColor: Color = colors.onBackground,
    axisColor: Color = colors.onBackground,
    labelTextSize: TextUnit = 24.sp
) {
    val currentAnimationState = remember {
        MutableTransitionState(AnimationProgress.START).apply {
            targetState = AnimationProgress.END
        }
    }

    val barAnimationTransition = updateTransition(
        transitionState = currentAnimationState,
        label = "bar_chart_transition"
    )

    val barHeight by barAnimationTransition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 2000,
                easing = FastOutSlowInEasing,
            )
        }, label = ""
    ) { progress ->
        if (progress == AnimationProgress.START) 0f
        else 300f
    }

    Canvas(
        modifier = modifier
            .padding(20.dp)
    ) {
        // todo calculate bar spacing with these values
        val (canvasWidth, canvasHeight) = size

        drawAxis(
            drawScope = this,
            axisColor = axisColor,
            axisType = Axis.X_AXIS
        )
        drawAxis(
            drawScope = this,
            axisColor = axisColor,
            axisType = Axis.Y_AXIS
        )

        drawRect(
            color = barColor,
            size = Size(width = 20f, height = barHeight),
            topLeft = Offset(100f, size.height - barHeight)
        )

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                "halloo",
                100f, 100f,
                android.graphics.Paint().apply {
                    textSize = labelTextSize.toPx()
                    color = axisColor.toLegacyInt()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    SimplePomodoroTheme {
        BarChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            dataPoints = listOf()
        )
    }
}