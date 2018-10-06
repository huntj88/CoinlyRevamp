package me.jameshunt.base

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface Repository {
    fun updateTimeRanges(base: CurrencyType, other: CurrencyType): Observable<Message>
    fun updateCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Single<Message>
    fun writeTransactions(transactions: List<Transaction>): Completable
}

interface KeyValueTool {
    fun set(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
}
