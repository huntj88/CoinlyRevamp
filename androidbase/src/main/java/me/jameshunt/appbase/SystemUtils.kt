package me.jameshunt.appbase

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt


object SystemUtils {

    fun dpToPx(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return dpToPx(dp.toFloat(), context).roundToInt()
    }
}