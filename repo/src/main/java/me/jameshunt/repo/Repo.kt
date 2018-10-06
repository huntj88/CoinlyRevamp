package me.jameshunt.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.CryptoCompare

class Repo(context: Any) : Repository {

    private val cryptoCompare = CryptoCompare()
    private val database = Database(context)

    override fun updateTimeRanges(base: CurrencyType, other: CurrencyType): Observable<Message> {
        return WriteTimeRange(cryptoCompare = cryptoCompare, database = database).update(base, other)
    }

    override fun updateCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Single<Message> {
        return cryptoCompare.getCurrentPrices(base = base, others = others).flatMap {
            when (it) {
                is DataSource.Success -> database
                        .writeTimePrice(it.data, Database.TimePriceUpdateCategory.CurrentPrice)
                        .toSingle { Message.Success("Current Prices Updated") }

                is DataSource.Error -> Single.just(Message.Error(it.message))
            }
        }
    }

    override fun writeTransactions(transactions: List<Transaction>): Completable {
        return database.writeTransactions(transactions)
    }
}
