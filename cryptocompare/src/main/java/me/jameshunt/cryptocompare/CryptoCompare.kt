package me.jameshunt.cryptocompare

import io.reactivex.Single
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.UnixMilliSeconds
import me.jameshunt.cryptocompare.domain.TimeRangeRaw

class CryptoCompare {

    private val client = ClientFactory().client

    fun getCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Single<Map<CurrencyType, Double>> {
        return client
                .getCurrentPrices(base = base, others = others.join())
                .map { it.json }
    }

    fun getHistoricalPrices(base: CurrencyType, others: Set<CurrencyType>, time: UnixMilliSeconds): Single<HistoricalPrice> {
        return client.getHistoricalPrice(base = base, others = others.join(), time = time)
    }

    fun getDailyPrices(base: CurrencyType, other: CurrencyType, numDaysAgo: Int): Single<TimeRangeRaw> {
        return client.getDailyPrices(base = base, other = other, numDaysAgo = numDaysAgo)
    }

    fun getHourlyPrices(base: CurrencyType, other: CurrencyType, numHoursAgo: Int): Single<TimeRangeRaw> {
        return client.getHourlyPrices(base = base, other = other, numHoursAgo = numHoursAgo)
    }

    fun getMinutePrices(base: CurrencyType, other: CurrencyType, numMinAgo: Int): Single<TimeRangeRaw> {
        return client.getMinutePrices(base = base, other = other, numMinAgo = numMinAgo)
    }

    private fun Set<CurrencyType>.join(): String = this.joinToString(separator = ",")
}