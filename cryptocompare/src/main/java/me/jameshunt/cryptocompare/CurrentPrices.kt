package me.jameshunt.cryptocompare

import com.squareup.moshi.FromJson
import me.jameshunt.base.CurrencyType

data class CurrentPrices(val json: Map<CurrencyType, Double>)

class CurrentPricesAdapter {
    @FromJson
    fun fromJson(json: Map<String, Double>): CurrentPrices {
        return CurrentPrices(json.mapKeys { CurrencyType.valueOf(it.key) })
    }
}