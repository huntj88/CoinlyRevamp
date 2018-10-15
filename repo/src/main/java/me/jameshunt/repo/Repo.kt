package me.jameshunt.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.CryptoCompare
import me.jameshunt.repo.db.domain.MyObjectBox

class Repo(context: Any) : Repository {

    private val box = MyObjectBox.builder().androidContext(context).build()
    private val timePriceDatabase = TimePriceDatabase(box)
    private val transactionDatabase = TransactionDatabase(box)

    private val cryptoCompare = CryptoCompare()

    override fun updateTimeRanges(base: CurrencyType, target: CurrencyType): Observable<Message> {
        return WriteTimeRange(cryptoCompare = cryptoCompare, timePriceDatabase = timePriceDatabase).update(base, target)
    }

    override fun updateExchangeRates(base: CurrencyType, targets: Set<CurrencyType>): Single<Message> {
        return cryptoCompare.getCurrentPrices(base = base, targets = targets).flatMap {
            when (it) {
                is DataSource.Success -> timePriceDatabase
                        .writeTimePrice(it.data, TimePriceDatabase.TimePriceUpdateCategory.ExchangeRate)
                        .toSingle { Message.Success("Current Prices Updated") }

                is DataSource.Error -> Single.just(Message.Error(it.message))
            }
        }
    }

    override fun getCurrentExchangeRate(base: CurrencyType, target: CurrencyType): Observable<DataSource<TimePrice>> {
        return timePriceDatabase.getCurrentExchangeRate(base, target)
    }

    // we need to specify the exchange
    override fun getExchangeRateAtTime(
            base: CurrencyType,
            target: CurrencyType,
            milliSeconds: UnixMilliSeconds,
            exchangeType: ExchangeType
    ): Single<DataSource<TimePrice>> {
        return timePriceDatabase.getExchangeRateAtTime(base, target, milliSeconds, exchangeType).flatMap { dataSource ->
            when (dataSource) {
                is DataSource.Success -> Single.just(dataSource)
                is DataSource.Error -> {
                    cryptoCompare
                            .getHistoricalPrices(base = base, targets = setOf(target), time = milliSeconds, exchange = exchangeType)
                            .flatMapCompletable {
                                when (it) {
                                    is DataSource.Error -> Completable.complete()
                                    is DataSource.Success -> timePriceDatabase
                                            .writeTimePrice(it.data, TimePriceDatabase.TimePriceUpdateCategory.ExchangeRate)
                                }
                            }
                            .andThen(timePriceDatabase.getExchangeRateAtTime(base, target, milliSeconds, exchangeType))
                }
            }
        }
    }

    override fun writeTransactions(transactions: List<Transaction>): Completable {
        return transactionDatabase.writeTransactions(transactions)
    }

    override fun readTransactions(currencyType: CurrencyType): Observable<DataSource<List<Transaction>>> {
        return transactionDatabase.readTransactions(currencyType)
    }
}
