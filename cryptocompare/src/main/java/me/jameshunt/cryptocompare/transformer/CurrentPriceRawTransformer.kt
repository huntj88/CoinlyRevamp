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
        val otherLocal = data.key
        val baseLocal = base
        val price = data.value

        return object : TimePrice {
            override val base: CurrencyType = baseLocal
            override val other: CurrencyType = otherLocal

            override val price: CurrencyAmount = price
            override val time: UnixMilliSeconds = System.currentTimeMillis()
        }
    }
}
