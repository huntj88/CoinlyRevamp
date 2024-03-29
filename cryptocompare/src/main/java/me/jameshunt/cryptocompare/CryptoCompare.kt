package me.jameshunt.cryptocompare

import io.reactivex.Single
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.transformer.CurrentPriceRawTransformer
import me.jameshunt.cryptocompare.transformer.HistoricalPriceRawTransformer
import me.jameshunt.cryptocompare.transformer.TimeRangeRawTransformer

class CryptoCompare {

    private val client = ClientFactory().client

    fun getCurrentPrices(base: CurrencyType, targets: Set<CurrencyType>): Single<DataSource<List<TimePrice>>> {
        return client
                .getCurrentPrices(base = base, targets = targets.joinCurrencies())
                .compose(CurrentPriceRawTransformer(base = base))
    }

    fun getHistoricalPrices(base: CurrencyType, targets: Set<CurrencyType>, time: UnixMilliSeconds, exchange: ExchangeType): Single<DataSource<List<TimePrice>>> {
        return client
                .getHistoricalPrice(
                        base = base,
                        targets = targets.joinCurrencies(),
                        time = time / 1000,
                        exchange = exchange.name.toLowerCase().capitalize()
                )
                .compose(HistoricalPriceRawTransformer(time = time, exchange = exchange))
    }

    fun getDailyPrices(base: CurrencyType, target: CurrencyType, numDaysAgo: Int): Single<DataSource<List<TimePrice>>> {
        return client.getDailyPrices(base = base, target = target, numDaysAgo = numDaysAgo)
                .compose(TimeRangeRawTransformer(base = base, target = target))
    }

    fun getHourlyPrices(base: CurrencyType, target: CurrencyType, numHoursAgo: Int): Single<DataSource<List<TimePrice>>> {
        return client.getHourlyPrices(base = base, target = target, numHoursAgo = numHoursAgo)
                .compose(TimeRangeRawTransformer(base = base, target = target))
    }

    fun getMinutePrices(base: CurrencyType, target: CurrencyType, numMinAgo: Int): Single<DataSource<List<TimePrice>>> {
        return client.getMinutePrices(base = base, target = target, numMinAgo = numMinAgo)
                .compose(TimeRangeRawTransformer(base = base, target = target))
    }

    private fun Set<CurrencyType>.joinCurrencies(): String = this.joinToString(separator = ",")
}
