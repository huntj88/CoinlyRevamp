package me.jameshunt.appbase.template.card.graph

import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import me.jameshunt.appbase.R
import me.jameshunt.appbase.template.card.CardLineChartData

/**
 * Created by James on 3/15/2018.
 */

class CardLineChart {
    companion object {
        fun create(data: CardLineChartData, parent: LinearLayout): View {
            val inflater = LayoutInflater.from(parent.context)
            val lineChart = inflater.inflate(R.layout.card_line_chart, parent, false) as LineChart

            return lineChart.apply {
                this.setViewPortOffsets(0f, 0f, 0f, 0f)
                listOf(this.xAxis, this.axisLeft, this.axisRight).handleAllAxis()

                val lineColor = ContextCompat.getColor(this.context, R.color.colorPrimary)
                this.data = getGraphData(data, lineColor)

                this.setDrawGridBackground(false)
                this.setPinchZoom(false)
                this.isHighlightPerTapEnabled = false
                this.isDoubleTapToZoomEnabled = false
                this.legend.isEnabled = false

                this.description = Description().apply { text = "" }

                this.invalidate()
            }
        }

        private fun getGraphData(data: CardLineChartData, color: Int): LineData {
            val points = data
                    .points
                    .let { it.smoothData() }
                    .map { Entry(it.x, it.y) }

            val lineDataSet = LineDataSet(points, "prices").apply {
                this.setDrawCircles(false)
                this.setDrawValues(false)
                this.setDrawHighlightIndicators(false)
                this.setDrawFilled(true)
                this.fillColor = color
                this.color = color
            }

            return LineData(lineDataSet)
        }

        private fun List<AxisBase>.handleAllAxis() {
            this.forEach {
                it.setDrawAxisLine(false)
                it.setDrawLabels(false)
                it.setDrawGridLines(false)
            }
        }

        private fun List<CardLineChartData.Point>.smoothData(): List<CardLineChartData.Point> {

            val earliestTime = this.firstOrNull()?.x ?: 0.0f
            val latestTime = this.lastOrNull()?.x ?: 0.0f

            val timeDiff = latestTime - earliestTime

            val minTimeBetweenPoints = timeDiff / 200

            data class Keep(
                    val points: List<CardLineChartData.Point> = listOf(),
                    val throwawaysToAverage: List<CardLineChartData.Point> = listOf()
            ) {
                fun averageRemainderForLastPoint(): Keep {
                    return when (this.throwawaysToAverage.isEmpty()) {
                        true -> this
                        false -> Keep(
                                points = this.points + this.throwawaysToAverage.average(),
                                throwawaysToAverage = listOf()
                        )
                    }
                }
            }

            return this
                    .fold(Keep()) { acc, point ->
                        acc.throwawaysToAverage
                                .firstOrNull()
                                ?.let { earliestInThisAverage ->
                                    when (point.x > earliestInThisAverage.x + minTimeBetweenPoints) {
                                        true -> Keep(
                                                points = acc.points + acc.throwawaysToAverage.average(),
                                                throwawaysToAverage = listOf(point)
                                        )
                                        false -> acc.copy(throwawaysToAverage = acc.throwawaysToAverage + point)
                                    }
                                } ?: acc.copy(throwawaysToAverage = listOf(point))
                    }
                    .averageRemainderForLastPoint()
                    .points
        }

        private fun List<CardLineChartData.Point>.average(): CardLineChartData.Point {
            return CardLineChartData.Point(
                    x = this.map { it.x }.average().toFloat(),
                    y = this.map { it.y }.average().toFloat()
            )
        }
    }
}