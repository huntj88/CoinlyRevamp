package me.jameshunt.cryptocompare

import io.reactivex.Single
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.TimePrice
import me.jameshunt.base.UnixMilliSeconds
import me.jameshunt.cryptocompare.transformer.CurrentPriceRawTransformer
import me.jameshunt.cryptocompare.transformer.HistoricalPriceRawTransformer
import me.jameshunt.cryptocompare.transformer.TimeRangeRawTransformer

class CryptoCompare {

    private val client = ClientFactory().client

    fun getCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Single<List<TimePrice>> {
        return client
                .getCurrentPrices(base = base, others = others.joinCurrencies())
                .compose(CurrentPriceRawTransformer(base = base))
    }

    fun getHistoricalPrices(base: CurrencyType, others: Set<CurrencyType>, time: UnixMilliSeconds): Single<List<TimePrice>> {
        return client.getHistoricalPrice(base = base, others = others.joinCurrencies(), time = time)
                .compose(HistoricalPriceRawTransformer(time = time))
    }

    fun getDailyPrices(base: CurrencyType, other: CurrencyType, numDaysAgo: Int): Single<List<TimePrice>> {
        return client.getDailyPrices(base = base, other = other, numDaysAgo = numDaysAgo)
                .compose(TimeRangeRawTransformer(base = base, other = other))
    }

    fun getHourlyPrices(base: CurrencyType, other: CurrencyType, numHoursAgo: Int): Single<List<TimePrice>> {
        return client.getHourlyPrices(base = base, other = other, numHoursAgo = numHoursAgo)
                .compose(TimeRangeRawTransformer(base = base, other = other))
    }

    fun getMinutePrices(base: CurrencyType, other: CurrencyType, numMinAgo: Int): Single<List<TimePrice>> {
        return client.getMinutePrices(base = base, other = other, numMinAgo = numMinAgo)
                .compose(TimeRangeRawTransformer(base = base, other = other))
    }

    private fun Set<CurrencyType>.joinCurrencies(): String = this.joinToString(separator = ",")
}
