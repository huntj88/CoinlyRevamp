package me.jameshunt.cryptocompare.transformer

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.HistoricalPrice

internal class HistoricalPriceRawTransformer(
        private val time: UnixMilliSeconds,
        private val exchange: ExchangeType
) : SingleTransformer<List<HistoricalPrice>, DataSource<TimePrice>> {
    override fun apply(upstream: Single<List<HistoricalPrice>>): SingleSource<DataSource<TimePrice>> {
        return upstream
                .map { list ->
                    list.first().let {

                        val time = this.time
                        val exchange = this.exchange

                        object : TimePrice {
                            override val base: CurrencyType = it.base
                            override val target: CurrencyType = it.target
                            override val price: CurrencyAmount = it.price
                            override val time: UnixMilliSeconds = time
                            override val exchange: ExchangeType = exchange
                        }
                    }
                }
                .map { DataSource.Success(it) as DataSource<TimePrice> }
    }
}