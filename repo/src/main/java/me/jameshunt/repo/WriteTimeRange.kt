package me.jameshunt.repo

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.CryptoCompare

internal class WriteTimeRange(private val database: Database, private val cryptoCompare: CryptoCompare) {

    fun update(base: CurrencyType, other: CurrencyType): Observable<Message> {
        return daily(base, other)
                .passMessageThenNextEvenIfError(hourly(base, other))
                .passMessageThenNextEvenIfError(minute(base, other))
    }

    private fun daily(base: CurrencyType, other: CurrencyType): Single<Message> {
        val current = System.currentTimeMillis()
        val numDaysAgo = (current - database.readLastDay()) / milliInDay

        return when (numDaysAgo > 0) {
            true -> cryptoCompare
                    .getDailyPrices(base = base, other = other, numDaysAgo = numDaysAgo.toInt())
                    .flatMap { it.writeTimePrices("Daily prices updated", Database.TimePriceUpdateCategory.Day) }
            false -> Single.just(Message.Success("$other: Daily prices already up to date"))
        }
    }

    private fun hourly(base: CurrencyType, other: CurrencyType): Single<Message> {
        val current = System.currentTimeMillis()
        val numHoursAgo = (current - database.readLastHour()) / milliInHour

        return when (numHoursAgo > 0) {
            true -> cryptoCompare
                    .getHourlyPrices(base = base, other = other, numHoursAgo = numHoursAgo.toInt())
                    .flatMap { it.writeTimePrices("Hourly prices updated", Database.TimePriceUpdateCategory.Hour) }
            false -> Single.just(Message.Success("$other: Hourly prices already up to date"))
        }
    }

    private fun minute(base: CurrencyType, other: CurrencyType): Single<Message> {
        val current = System.currentTimeMillis()
        val numMinAgo = (current - database.readLastMinute()) / milliInMinute

        return when (numMinAgo > 0) {
            true -> cryptoCompare
                    .getMinutePrices(base = base, other = other, numMinAgo = numMinAgo.toInt())
                    .flatMap { it.writeTimePrices("Minute prices Updated", Database.TimePriceUpdateCategory.Min) }
            false -> Single.just(Message.Success("$other: Minute prices already up to date"))
        }
    }

    private fun DataSource<List<TimePrice>>.writeTimePrices(successMessage: String, updateCategory: Database.TimePriceUpdateCategory): Single<Message> {
        return when (this) {
            is DataSource.Success -> database
                    .writeTimePrice(this.data, updateCategory)
                    .observeOn(Schedulers.io())
                    .toSingle { Message.Success(successMessage) }

            is DataSource.Error -> Single.just(Message.Error(this.message))
        }
    }
}