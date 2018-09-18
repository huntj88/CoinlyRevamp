package me.jameshunt.cryptocompare

import com.squareup.moshi.FromJson
import me.jameshunt.base.CurrencyType

data class CurrentPricesRaw(val json: Map<CurrencyType, Double>)

class CurrentPricesRawAdapter {
    @FromJson
    fun fromJson(json: Map<String, Double>): CurrentPricesRaw {
        return CurrentPricesRaw(json.mapKeys { CurrencyType.valueOf(it.key) })
    }
}