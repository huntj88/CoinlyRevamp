package me.jameshunt.cryptocompare

import com.squareup.moshi.FromJson
import me.jameshunt.base.CurrencyType

data class HistoricalPrice(val base: CurrencyType, val other: CurrencyType, val price: Double)

class HistoricalPriceRawAdapter {
    @FromJson
    fun fromJson(json: Map<String, Map<String, Double>>): HistoricalPrice {

        return json.map {
            val currencyType = CurrencyType.valueOf(it.key)
            val otherCurrency = it.value.keys.map { CurrencyType.valueOf(it) }[0]
            val price = it.value.values.map { it }[0]

            HistoricalPrice(currencyType, otherCurrency, price)
        }[0]
    }
}