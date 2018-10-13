package me.jameshunt.repo

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.*
import me.jameshunt.cryptocompare.CryptoCompare

internal class WriteTimeRange(private val timePriceDatabase: TimePriceDatabase, private val cryptoCompare: CryptoCompare) {

    fun update(base: CurrencyType, target: CurrencyType): Observable<Message> {
        return daily(base, target)
                .passMessageThenNextEvenIfError(hourly(base, target))
                .passMessageThenNextEvenIfError(minute(base, target))
    }

    private fun daily(base: CurrencyType, target: CurrencyType): Single<Message> {
        val current = System.currentTimeMillis()
        val numDaysAgo = (current - timePriceDatabase.readLastDay(base = base, target = target)) / milliInDay

        return when (numDaysAgo > 0) {
            true -> cryptoCompare
                    .getDailyPrices(base = base, target = target, numDaysAgo = numDaysAgo.toInt())
                    .flatMap { it.writeTimePrices("$target: Daily prices updated", TimePriceDatabase.TimePriceUpdateCategory.Day) }
            false -> Single.just(Message.Success("$target: Daily prices already up to date"))
        }
    }

    private fun hourly(base: CurrencyType, target: CurrencyType): Single<Message> {
        val current = System.currentTimeMillis()
        val numHoursAgo = (current - timePriceDatabase.readLastHour(base = base, target = target)) / milliInHour

        return when (numHoursAgo > 0) {
            true -> cryptoCompare
                    .getHourlyPrices(base = base, target = target, numHoursAgo = numHoursAgo.toInt())
                    .flatMap { it.writeTimePrices("$target: Hourly prices updated", TimePriceDatabase.TimePriceUpdateCategory.Hour) }
            false -> Single.just(Message.Success("$target: Hourly prices already up to date"))
        }
    }

    private fun minute(base: CurrencyType, target: CurrencyType): Single<Message> {
        val current = System.currentTimeMillis()
        val numMinAgo = (current - timePriceDatabase.readLastMinute(base = base, target = target)) / milliInMinute

        return when (numMinAgo > 0) {
            true -> cryptoCompare
                    .getMinutePrices(base = base, target = target, numMinAgo = numMinAgo.toInt())
                    .flatMap { it.writeTimePrices("$target: Minute prices Updated", TimePriceDatabase.TimePriceUpdateCategory.Min) }
            false -> Single.just(Message.Success("$target: Minute prices already up to date"))
        }
    }

    private fun DataSource<List<TimePrice>>.writeTimePrices(successMessage: String, updateCategory: TimePriceDatabase.TimePriceUpdateCategory): Single<Message> {
        return when (this) {
            is DataSource.Success -> timePriceDatabase
                    .writeTimePrice(this.data, updateCategory)
                    .observeOn(Schedulers.io())
                    .toSingle { Message.Success(successMessage) }

            is DataSource.Error -> Single.just(Message.Error(this.message))
        }
    }
}