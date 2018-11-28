package me.jameshunt.cryptocompare

import io.reactivex.Single
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.transformer.*

class CryptoCompare {

    private val client = ClientFactory().client

    fun getCurrentPrices(base: CurrencyType, targets: Set<CurrencyType>): Single<DataSource<List<TimePrice>>> {
        return client
                .getCurrentPrices(base = base, targets = targets.joinCurrencies())
                .compose(CurrentPriceRawTransformer(base = base))
                .compose(FixExchangeRateListTransformer())
    }

    // this endpoint can take a set of currencyTypes, but i only use one. easier to switch the base and target if no pair
    // coinbase ETH-USD works, but coinbase USD-ETH does not
    fun getHistoricalPrices(base: CurrencyType, target: CurrencyType, time: UnixMilliSeconds, exchange: ExchangeType): Single<DataSource<TimePrice>> {
        return client
                .getHistoricalPrice(
                        base = base,
                        target = target,
                        time = time / 1000,
                        exchange = exchange.name.toLowerCase().capitalize()
                )
                .compose(HistoricalPriceRawTransformer(time = time, exchange = exchange))
                .compose(FixExchangeRateTransformer())
                .onErrorReturn {

                    //if error then switch base and target

                    client
                            .getHistoricalPrice(
                                    base = target,
                                    target = base,
                                    time = time / 1000,
                                    exchange = exchange.name.toLowerCase().capitalize()
                            )
                            .compose(HistoricalPriceRawTransformer(time = time, exchange = exchange))
                            //z.onErrorReturn { DataSource.Error("Could not get price in past") }
                            .blockingGet() // already on io thread
                            // doesn't need the FixExchangeRateTransformer because already correct
                }
    }

    fun getDailyPrices(base: CurrencyType, target: CurrencyType, numDaysAgo: Int): Single<DataSource<List<TimePrice>>> {
        return client.getDailyPrices(base = base, target = target, numDaysAgo = numDaysAgo)
                .compose(TimeRangeRawTransformer(base = base, target = target))
                .compose(FixExchangeRateListTransformer())
    }

    fun getHourlyPrices(base: CurrencyType, target: CurrencyType, numHoursAgo: Int): Single<DataSource<List<TimePrice>>> {
        return client.getHourlyPrices(base = base, target = target, numHoursAgo = numHoursAgo)
                .compose(TimeRangeRawTransformer(base = base, target = target))
                .compose(FixExchangeRateListTransformer())
    }

    fun getMinutePrices(base: CurrencyType, target: CurrencyType, numMinAgo: Int): Single<DataSource<List<TimePrice>>> {
        return client.getMinutePrices(base = base, target = target, numMinAgo = numMinAgo)
                .compose(TimeRangeRawTransformer(base = base, target = target))
                .compose(FixExchangeRateListTransformer())
    }

    private fun Set<CurrencyType>.joinCurrencies(): String = this.joinToString(separator = ",")
}
