package me.jameshunt.base

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface Repository {
    fun updateTimeRanges(base: CurrencyType, target: CurrencyType): Observable<Message>
    fun updateExchangeRates(base: CurrencyType, targets: Set<CurrencyType>): Single<Message>
    fun getCurrentExchangeRate(base: CurrencyType, target: CurrencyType): Observable<DataSource<TimePrice>>

    fun writeTransactions(transactions: List<Transaction>): Completable
    fun readTransactions(currencyType: CurrencyType): Observable<DataSource<List<Transaction>>>
}

interface KeyValueTool {
    fun set(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
}
