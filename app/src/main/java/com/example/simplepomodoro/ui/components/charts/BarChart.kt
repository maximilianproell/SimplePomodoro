package com.example.simplepomodoro.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplepomodoro.ui.components.charts.utils.*
import com.example.simplepomodoro.ui.theme.SimplePomodoroTheme
import kotlin.math.ceil

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    dataPoints: List<ChartDataPoint>,
    maxYValue: Float,
    barColor: Color = colors.onBackground,
    barWidthDp: Dp = 12.dp,
    axisColor: Color = colors.onBackground,
    labelTextSize: TextUnit = 12.sp
) {
    val currentAnimationState = remember(dataPoints) {
        MutableTransitionState(AnimationProgress.START).apply {
            targetState = AnimationProgress.END
        }
    }

    val barAnimationTransition = updateTransition(
        transitionState = currentAnimationState,
        label = "bar_chart_transition"
    )

    val barPercentageHeights = dataPoints.map { dataPoint ->
        barAnimationTransition.animateFloat(
            transitionSpec = {
                tween(
                    delayMillis = 500,
                    durationMillis = 2000,
                    easing = FastOutSlowInEasing,
                )
            }, label = dataPoint.xValue
        ) { progress ->
            if (progress == AnimationProgress.START) 0f
            else dataPoint.yValue / maxYValue
        }
    }

    Box(modifier = modifier.padding(12.dp)) {
        Canvas(
            modifier = modifier
        ) {
            val (canvasWidth, canvasHeight) = size
            val spaceForLabel = 24.dp.toPx()
            val chartWidth = canvasWidth - spaceForLabel
            val chartHeight = canvasHeight - spaceForLabel

            // calculate bar spacing in px
            val chartPadding = 12.dp.toPx()
            // 2 * chart padding + space for Label at start
            val barSpaceHorizontal = chartWidth - (chartPadding * 2)
            val sizeOccupiedByBars = barWidthDp.toPx() * dataPoints.size
            val barSpacing = (barSpaceHorizontal - sizeOccupiedByBars) / (dataPoints.size - 1)

            drawAxis(
                drawScope = this,
                axisColor = axisColor,
                axisType = Axis.X_AXIS,
                offsetPadding = spaceForLabel
            )
            drawAxis(
                drawScope = this,
                axisColor = axisColor,
                axisType = Axis.Y_AXIS,
                offsetPadding = spaceForLabel
            )

            var currentBarOffset = chartPadding + spaceForLabel
            dataPoints.forEachIndexed { index, dataPoint ->
                val barHeight = barPercentageHeights[index].value * chartHeight
                drawRect(
                    color = barColor,
                    size = Size(width = barWidthDp.toPx(), height = barHeight),
                    topLeft = Offset(currentBarOffset, chartHeight - barHeight)
                )

                drawYAxisLabel(
                    drawScope = this,
                    labelColor = axisColor,
                    text = dataPoint.xValue,
                    labelTextSizePx = labelTextSize.toPx(),
                    xPosition = currentBarOffset,
                    yPosition = canvasHeight
                )

                currentBarOffset += barSpacing + barWidthDp.toPx()
            }

            // draw highest y value label
            drawYAxisLabel(
                drawScope = this,
                labelColor = axisColor,
                text = "${ceil(maxYValue).toInt()}h",
                labelTextSizePx = labelTextSize.toPx(),
                xPosition = 0f,
                yPosition = 12.dp.toPx(),
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
            dataPoints = listOf(
                ChartDataPoint("MO", 8.5f),
                ChartDataPoint("DI", 10.5f),
                ChartDataPoint("MI", 8.5f),
                ChartDataPoint("DO", 4.5f),
                ChartDataPoint("FR", 3.0f),
                ChartDataPoint("SA", 8.25f),
                ChartDataPoint("SO", 12.0f),
            ),
            // let's just say, 12h is the maximum one would work a day
            maxYValue = 12.0f
        )
    }
}