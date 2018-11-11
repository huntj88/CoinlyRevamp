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
            val layoutInflater = LayoutInflater.from(parent.context)
            val lineChart = layoutInflater.inflate(R.layout.card_line_chart, parent, false) as LineChart

            lineChart.setViewPortOffsets(0f, 0f, 0f, 0f)

            val lineColor = ContextCompat.getColor(lineChart.context, R.color.colorPrimary)
            lineChart.data = getGraphData(data, lineColor)

            handleAllAxis(listOf(lineChart.xAxis, lineChart.axisLeft, lineChart.axisRight))

            lineChart.setDrawGridBackground(false)
            lineChart.setPinchZoom(false)
            lineChart.isHighlightPerTapEnabled = false
            lineChart.isDoubleTapToZoomEnabled = false
            lineChart.legend.isEnabled = false

            lineChart.description = Description().apply { text = "" }

            lineChart.invalidate()

            return lineChart
        }

        private fun getGraphData(data: CardLineChartData, color: Int): LineData {

            val numPointsWanted = 100
            val numPointsWeHave = data.pricesOverTime.size

            val test = numPointsWeHave / numPointsWanted

            val points = data.pricesOverTime
                    .filterIndexed { index, _ ->
                        test == 0 || index % test == 0
                    }.map { timePrice ->
                        val xValue = timePrice.time / 10000000.0
                        Entry(xValue.toFloat(), 1/timePrice.price.toFloat())
                    }


            val lineDataSet = LineDataSet(points, "prices")

            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawValues(false)
            lineDataSet.setDrawHighlightIndicators(false)
            lineDataSet.setDrawFilled(true)

            lineDataSet.fillColor = color
            lineDataSet.color = color

            return LineData(lineDataSet)
        }

        private fun handleAllAxis(axisList: List<AxisBase>) {
            axisList.forEach {
                it.setDrawAxisLine(false)
                it.setDrawLabels(false)
                it.setDrawGridLines(false)
            }
        }
    }
}