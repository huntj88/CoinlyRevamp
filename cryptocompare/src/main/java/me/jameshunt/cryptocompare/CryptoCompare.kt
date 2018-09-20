package me.jameshunt.cryptocompare

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.CurrencyAmount
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.TimePrice
import me.jameshunt.base.UnixMilliSeconds
import me.jameshunt.cryptocompare.domain.TimePriceRaw
import me.jameshunt.cryptocompare.domain.TimeRangeRaw

class CryptoCompare {

    private val client = ClientFactory().client

    fun getCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Single<Map<CurrencyType, CurrencyAmount>> {
        return client
                .getCurrentPrices(base = base, others = others.join())
                .map { it.json }
    }

    fun getHistoricalPrices(base: CurrencyType, others: Set<CurrencyType>, time: UnixMilliSeconds): Single<List<TimePrice>> {
        return client.getHistoricalPrice(base = base, others = others.join(), time = time)
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

    private fun Set<CurrencyType>.join(): String = this.joinToString(separator = ",")
}


internal class TimeRangeRawTransformer(
        private val base: CurrencyType,
        private val other: CurrencyType
) : SingleTransformer<TimeRangeRaw, List<TimePrice>> {
    override fun apply(upstream: Single<TimeRangeRaw>): SingleSource<List<TimePrice>> {
        val baseLocal = base
        val otherLocal = other

        return upstream.map {
            it.Data.map { raw ->
                object : TimePrice {
                    private val data: TimePriceRaw = raw

                    override val base: CurrencyType = baseLocal
                    override val other: CurrencyType = otherLocal

                    override val price: CurrencyAmount = data.close
                    override val time: UnixMilliSeconds = data.time * 1000L
                }
            }
        }
    }
}

internal class HistoricalPriceRawTransformer(
        private val time: UnixMilliSeconds
) : SingleTransformer<List<HistoricalPriceRaw>, List<TimePrice>> {
    override fun apply(upstream: Single<List<HistoricalPriceRaw>>): SingleSource<List<TimePrice>> {
        val timeLocal = time

        return upstream.map {
            it.map { raw ->
                object : TimePrice {
                    private val data: HistoricalPriceRaw = raw

                    override val base: CurrencyType = data.base
                    override val other: CurrencyType = data.other

                    override val price: CurrencyAmount = data.price
                    override val time: UnixMilliSeconds = timeLocal
                }
            }
        }
    }
}
