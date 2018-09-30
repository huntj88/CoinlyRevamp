package me.jameshunt.base

import io.reactivex.Completable

interface Repository {
    fun updateTimeRanges(base: CurrencyType, other: CurrencyType): Completable
    fun updateCurrentPrices(base: CurrencyType, others: Set<CurrencyType>): Completable
    fun writeTransactions(transactions: List<Transaction>): Completable
}

interface KeyValueTool {
    fun set(key: String, value: String)
    fun get(key: String): String?
}