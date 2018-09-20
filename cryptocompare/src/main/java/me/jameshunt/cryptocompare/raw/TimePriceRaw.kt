package me.jameshunt.cryptocompare.raw

import com.squareup.moshi.Json

data class TimePriceRaw(
        val time: Int,
        val close: Double,
        val high: Double,
        val low: Double,
        val open: Double,
        @Json(name = "volumefrom")
        val volumeFrom: Double,
        @Json(name = "volumeto")
        val volumeTo: Double
)