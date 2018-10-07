package me.jameshunt.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.CryptoCompare

class Repo(context: Any) : Repository {

    private val cryptoCompare = CryptoCompare()
    private val database = Database(context)

    override fun updateTimeRanges(base: CurrencyType, target: CurrencyType): Observable<Message> {
        return WriteTimeRange(cryptoCompare = cryptoCompare, database = database).update(base, target)
    }

    override fun updateExchangeRates(base: CurrencyType, targets: Set<CurrencyType>): Single<Message> {
        return cryptoCompare.getCurrentPrices(base = base, targets = targets).flatMap {
            when (it) {
                is DataSource.Success -> database
                        .writeTimePrice(it.data, Database.TimePriceUpdateCategory.ExchangeRate)
                        .toSingle { Message.Success("Current Prices Updated") }

                is DataSource.Error -> Single.just(Message.Error(it.message))
            }
        }
    }

    override fun writeTransactions(transactions: List<Transaction>): Completable {
        return database.writeTransactions(transactions)
    }

    override fun getCurrentExchangeRate(base: CurrencyType, target: CurrencyType): Observable<DataSource<TimePrice>> {
        return database.getCurrentExchangeRate(base, target)
    }
}
