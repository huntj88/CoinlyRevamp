package me.jameshunt.repo

import io.reactivex.Completable
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.Repository
import me.jameshunt.base.Transaction
import me.jameshunt.cryptocompare.CryptoCompare

class Repo(context: Any): Repository {

    private val cryptoCompare = CryptoCompare()
    private val database = Database(context)

    override fun updateTimeRanges(base: CurrencyType, other: CurrencyType): Completable {
        return WriteTimeRange(cryptoCompare = cryptoCompare, database = database).update(base, other)
    }

    override fun updateCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Completable {
        return cryptoCompare.getCurrentPrices(base = base, others = others).flatMapCompletable {
            database.writeTimePrice(it, Database.TimePriceUpdateCategory.CurrentPrice)
        }
    }

    override fun writeTransactions(transactions: List<Transaction>): Completable {
        return database.writeTransactions(transactions)
    }
}
