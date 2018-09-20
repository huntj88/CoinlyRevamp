package me.jameshunt.cryptocompare

import com.squareup.moshi.FromJson
import me.jameshunt.base.CurrencyAmount
import me.jameshunt.base.CurrencyType

data class HistoricalPriceRaw(val base: CurrencyType, val other: CurrencyType, val price: CurrencyAmount)

class HistoricalPriceRawAdapter {
    @FromJson
    fun fromJson(json: Map<String, Map<String, Double>>): List<HistoricalPriceRaw> {
        return json.map {
            val currencyType = CurrencyType.valueOf(it.key)

            it.value.map {
                val other = CurrencyType.valueOf(it.key)
                val price = it.value
                HistoricalPriceRaw(currencyType, other, price)
            }
        }[0]
    }
}