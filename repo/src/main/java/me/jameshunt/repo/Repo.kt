package me.jameshunt.repo

import io.reactivex.Completable
import me.jameshunt.base.CurrencyType
import me.jameshunt.cryptocompare.CryptoCompare

class Repo(context: Any) {

    private val cryptoCompare = CryptoCompare()
    private val database = Database(context)

    fun updateTimeRanges(base: CurrencyType, other: CurrencyType): Completable {
        return WriteTimeRange(cryptoCompare = cryptoCompare, database = database).update(base, other)
    }
}