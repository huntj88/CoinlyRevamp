package me.jameshunt.cryptocompare.transformer

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.*

class FixExchangeRateTransformer : SingleTransformer<DataSource<TimePrice>, DataSource<TimePrice>> {
    override fun apply(upstream: Single<DataSource<TimePrice>>): SingleSource<DataSource<TimePrice>> {
        return upstream.map { dataSource ->
            dataSource.mapSuccess { it.fixPrice() }
        }
    }
}

class FixExchangeRateListTransformer : SingleTransformer<DataSource<List<TimePrice>>, DataSource<List<TimePrice>>> {
    override fun apply(upstream: Single<DataSource<List<TimePrice>>>): SingleSource<DataSource<List<TimePrice>>> {
        return upstream.map { dataSource ->
            dataSource.mapSuccess { timePrices ->
                timePrices.map { it.fixPrice() }
            }
        }
    }
}

private fun TimePrice.fixPrice(): TimePrice {
    return object : TimePrice {
        override val time: UnixMilliSeconds = this@fixPrice.time

        override val base: CurrencyType = this@fixPrice.base

        override val target: CurrencyType = this@fixPrice.target

        override val price: CurrencyAmount = 1.0 / this@fixPrice.price

        override val exchange: ExchangeType = this@fixPrice.exchange
    }
}