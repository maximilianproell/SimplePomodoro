package com.example.simplepomodoro.ui.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.simplepomodoro.ui.components.charts.BarChart

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val chartDataAsState =
        viewModel.chartDataForAllWorkPackagesFlow.collectAsState(initial = listOf())

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("This chart shows the total amount of worked hours per day.")
            Spacer(modifier = Modifier.height(16.dp))
            BarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                dataPoints = chartDataAsState.value,
                maxYValue = chartDataAsState.value.maxOfOrNull { it.yValue } ?: 12f,
            )
        }
    }
}