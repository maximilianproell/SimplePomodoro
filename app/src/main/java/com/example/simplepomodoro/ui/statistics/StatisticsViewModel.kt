package com.example.simplepomodoro.ui.statistics

import androidx.lifecycle.ViewModel
import com.example.simplepomodoro.data.DataRepository
import com.example.simplepomodoro.ui.components.charts.utils.ChartDataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.MonthDay
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(repository: DataRepository) :
    ViewModel() {
    val chartDataForAllWorkPackagesFlow = repository
        .getAllWorkPackagesAsFlow()
        .map { workPackages ->
            // filtering based on day of week so we can draw bar chart
            val mondays = workPackages.filter { it.date.dayOfWeek == DayOfWeek.MONDAY }
            val tuesdays = workPackages.filter { it.date.dayOfWeek == DayOfWeek.TUESDAY }
            val wednesdays = workPackages.filter { it.date.dayOfWeek == DayOfWeek.WEDNESDAY }
            val thursdays = workPackages.filter { it.date.dayOfWeek == DayOfWeek.THURSDAY }
            val fridays = workPackages.filter { it.date.dayOfWeek == DayOfWeek.FRIDAY }
            val saturdays = workPackages.filter { it.date.dayOfWeek == DayOfWeek.SATURDAY }
            val sundays = workPackages.filter { it.date.dayOfWeek == DayOfWeek.SUNDAY }

            val workPackagesForEveryDayOfWeek =
                listOf(mondays, tuesdays, wednesdays, thursdays, fridays, saturdays, sundays)

            workPackagesForEveryDayOfWeek.mapIndexed { index, workPackagesAtDayOfWeek ->
                ChartDataPoint(
                    xValue = DayOfWeek.of(index + 1).toString().take(2),
                    yValue = workPackagesAtDayOfWeek.sumOf { it.secondsWorked } / 60f / 60f
                )
            }

        }
}