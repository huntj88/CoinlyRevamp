package me.jameshunt.cryptocompare

import io.reactivex.Single
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.UnixMilliSeconds

class CryptoCompare {

    private val client = ClientFactory().client

    fun getCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Single<Map<CurrencyType, Double>> {
        return client
                .getCurrentPrices(base = base.name, others = others.joinToString(separator = ","))
                .map { it.json }
    }

    fun getHistoricalPrices(base: CurrencyType, others: CurrencyType, time: UnixMilliSeconds): Single<HistoricalPrice> {
        return client.getHistoricalPrice(base = base.name, others = others.name, time = time)
    }
}