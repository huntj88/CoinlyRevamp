package me.jameshunt.repo

import io.reactivex.Completable
import me.jameshunt.base.CurrencyType
import me.jameshunt.cryptocompare.CryptoCompare

class Repo(context: Any) {

    private val cryptoCompare = CryptoCompare()
    private val database = Database(context)

    fun updateTimeRanges(base: CurrencyType, other: CurrencyType): Completable {

        val current = System.currentTimeMillis()

        val milliInDay = 86_400_000
        val numDaysAgo = (current - database.readLastDay()) / milliInDay
        return cryptoCompare.getDailyPrices(base = base, other = other, numDaysAgo = numDaysAgo.toInt())
                .flatMapCompletable { database.writeTimePrice(it) }

//        // todo: only get enough to fill graphs
//        val milliInHour = 3_600_000
//        val numHoursAgo = (current - lastUpdatedHour) / milliInHour
//        cryptoCompare.getDailyPrices(base = base, other = other, numDaysAgo = numHoursAgo.toInt())
//
//        // todo: only get enough to fill graphs
//        val milliInMinute = 60_000
//        val numMinAgo = (current - lastUpdatedMin) / milliInMinute
//        cryptoCompare.getDailyPrices(base = base, other = other, numDaysAgo = numMinAgo.toInt())
    }

}