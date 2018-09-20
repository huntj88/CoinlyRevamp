package me.jameshunt.cryptocompare.transformer

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.CurrencyAmount
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.TimePrice
import me.jameshunt.base.UnixMilliSeconds
import me.jameshunt.cryptocompare.CurrentPrices

internal class CurrentPriceRawTransformer(private val base: CurrencyType) : SingleTransformer<CurrentPrices, List<TimePrice>> {
    override fun apply(upstream: Single<CurrentPrices>): SingleSource<List<TimePrice>> {

        return upstream.map { currentPrices ->
            currentPrices.json.map {
                val otherLocal = it.key
                val baseLocal = base
                val price = it.value

                object : TimePrice {
                    override val base: CurrencyType = baseLocal
                    override val other: CurrencyType = otherLocal

                    override val price: CurrencyAmount = price
                    override val time: UnixMilliSeconds = System.currentTimeMillis()
                }
            }
        }
    }
}
