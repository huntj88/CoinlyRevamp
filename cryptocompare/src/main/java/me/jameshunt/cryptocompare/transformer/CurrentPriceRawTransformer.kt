package me.jameshunt.cryptocompare.transformer

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.CurrentPrices

internal class CurrentPriceRawTransformer(
        private val base: CurrencyType
) : SingleTransformer<CurrentPrices, DataSource<List<TimePrice>>> {

    override fun apply(upstream: Single<CurrentPrices>): SingleSource<DataSource<List<TimePrice>>> = upstream
            .map { currentPrices -> currentPrices.json.map { mapJson(it) } }
            .map { DataSource.Success(it) as DataSource<List<TimePrice>> }
            .onErrorReturn { DataSource.Error("Could not Update current prices") }

    private fun mapJson(data: Map.Entry<CurrencyType, Double>): TimePrice {
        return object : TimePrice {
            override val base: CurrencyType = this@CurrentPriceRawTransformer.base
            override val target: CurrencyType = data.key

            override val price: CurrencyAmount = data.value
            override val time: UnixMilliSeconds = System.currentTimeMillis()
            override val exchange: ExchangeType = ExchangeType.NONE
        }
    }
}
