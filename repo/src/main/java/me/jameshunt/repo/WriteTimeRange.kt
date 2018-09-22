package me.jameshunt.repo

import io.reactivex.Completable
import me.jameshunt.base.CurrencyType
import me.jameshunt.cryptocompare.CryptoCompare

internal class WriteTimeRange(private val database: Database, private val cryptoCompare: CryptoCompare) {

    fun update(base: CurrencyType, other: CurrencyType): Completable {
        return daily(base, other)
        .andThen(hourly(base, other))
        .andThen(minute(base, other))
    }

    private fun daily(base: CurrencyType, other: CurrencyType): Completable {
        val current = System.currentTimeMillis()
        val numDaysAgo = (current - database.readLastDay()) / milliInDay

        return when (numDaysAgo > 0) {
            true -> cryptoCompare
                    .getDailyPrices(base = base, other = other, numDaysAgo = numDaysAgo.toInt())
                    .flatMapCompletable { database.writeTimePrice(it, Database.TimePriceUpdateCategory.Day) }
            false -> Completable.complete()
        }
    }

    private fun hourly(base: CurrencyType, other: CurrencyType): Completable {
        val current = System.currentTimeMillis()
        val numHoursAgo = (current - database.readLastHour()) / milliInHour

        return when(numHoursAgo > 0) {
            true -> cryptoCompare
                    .getHourlyPrices(base = base, other = other, numHoursAgo = numHoursAgo.toInt())
                    .flatMapCompletable { database.writeTimePrice(it, Database.TimePriceUpdateCategory.Hour) }
            false -> Completable.complete()
        }
    }

    private fun minute(base: CurrencyType, other: CurrencyType): Completable {
        val current = System.currentTimeMillis()
        val numMinAgo = (current - database.readLastMinute()) / milliInMinute

        return when(numMinAgo > 0) {
            true -> cryptoCompare
                    .getMinutePrices(base = base, other = other, numMinAgo = numMinAgo.toInt())
                    .flatMapCompletable { database.writeTimePrice(it, Database.TimePriceUpdateCategory.Min) }
            false -> Completable.complete()
        }
    }
}