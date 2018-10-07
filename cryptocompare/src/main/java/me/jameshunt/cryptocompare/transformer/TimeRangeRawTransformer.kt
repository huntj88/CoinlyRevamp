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
        val baseLocal = base
        val targetLocal = target

        return upstream
                .map { it.Data.map { raw -> raw.mapTimeRange(baseLocal, targetLocal) } }
                .map { DataSource.Success(it) as DataSource<List<TimePrice>> }
                .onErrorReturn { DataSource.Error("Could not update Time Range Prices") }
    }

    private fun TimePriceRaw.mapTimeRange(baseLocal: CurrencyType, targetLocal: CurrencyType): TimePrice {
        val data: TimePriceRaw = this
        return object : TimePrice {
            override val base: CurrencyType = baseLocal
            override val target: CurrencyType = targetLocal

            override val price: CurrencyAmount = data.close
            override val time: UnixMilliSeconds = data.time * 1000L
        }
    }
}
