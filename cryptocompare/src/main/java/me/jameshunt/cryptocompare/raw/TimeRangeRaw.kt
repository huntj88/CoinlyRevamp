package me.jameshunt.cryptocompare.raw

data class TimeRangeRaw(
        val Response: String,
        val Type: Int,
        val Aggregated: Boolean,
        val Data: List<TimePriceRaw>,
        val TimeTo: Int,
        val TimeFrom: Int,
        val FirstValueInArray: Boolean,
        val ConversionType: ConversionType
)