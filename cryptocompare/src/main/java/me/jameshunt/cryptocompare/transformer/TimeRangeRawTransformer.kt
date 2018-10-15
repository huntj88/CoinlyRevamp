package me.jameshunt.cryptocompare.transformer

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.raw.TimePriceRaw
import me.jameshunt.cryptocompare.raw.TimeRangeRaw

internal class TimeRangeRawTransformer(
        private val base: CurrencyType,
        private val target: CurrencyType
) : SingleTransformer<TimeRangeRaw, DataSource<List<TimePrice>>> {
    override fun apply(upstream: Single<TimeRangeRaw>): SingleSource<DataSource<List<TimePrice>>> {
        return upstream
                .map { it.Data.map { raw -> raw.mapTimeRange(this@TimeRangeRawTransformer.base, this@TimeRangeRawTransformer.target) } }
                .map { DataSource.Success(it) as DataSource<List<TimePrice>> }
                .onErrorReturn { DataSource.Error("Could not update Time Range Prices") }
    }

    private fun TimePriceRaw.mapTimeRange(base: CurrencyType, target: CurrencyType): TimePrice {
        return object : TimePrice {
            override val base: CurrencyType = base
            override val target: CurrencyType = target

            override val price: CurrencyAmount = this@mapTimeRange.close
            override val time: UnixMilliSeconds = this@mapTimeRange.time * 1000L
            override val exchange: ExchangeType = ExchangeType.NONE
        }
    }
}
