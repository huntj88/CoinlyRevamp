package me.jameshunt.cryptocompare.transformer

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.CurrencyAmount
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.TimePrice
import me.jameshunt.base.UnixMilliSeconds
import me.jameshunt.cryptocompare.raw.TimePriceRaw
import me.jameshunt.cryptocompare.raw.TimeRangeRaw

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