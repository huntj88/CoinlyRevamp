package me.jameshunt.cryptocompare.transformer

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.CurrencyAmount
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.TimePrice
import me.jameshunt.base.UnixMilliSeconds
import me.jameshunt.cryptocompare.HistoricalPrice

internal class HistoricalPriceRawTransformer(private val time: UnixMilliSeconds) : SingleTransformer<List<HistoricalPrice>, List<TimePrice>> {
    override fun apply(upstream: Single<List<HistoricalPrice>>): SingleSource<List<TimePrice>> {
        val timeLocal = time

        return upstream.map {
            it.map { raw ->
                object : TimePrice {
                    private val data: HistoricalPrice = raw

                    override val base: CurrencyType = data.base
                    override val target: CurrencyType = data.target

                    override val price: CurrencyAmount = data.price
                    override val time: UnixMilliSeconds = timeLocal
                }
            }
        }
    }
}