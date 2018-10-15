package me.jameshunt.repo

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.*
import me.jameshunt.repo.db.CurrencyTypeConverter
import me.jameshunt.repo.db.ExchangeTypeConverter
import me.jameshunt.repo.db.domain.*

internal class TimePriceDatabase(private val box: BoxStore) {

    fun writeTimePrice(timePrices: List<TimePrice>, updateCategory: TimePriceUpdateCategory): Completable {
        return Completable.fromAction {
            val timePriceBox = box.boxFor<TimePriceObjectBox>()

            val currencyTypeConverter = CurrencyTypeConverter()
            val base = currencyTypeConverter.convertToDatabaseValue(timePrices.first().base)
            val target = currencyTypeConverter.convertToDatabaseValue(timePrices.first().target)

            box.runInTx {
                val existingPrices =
                        timePriceBox
                                .query()
                                .between(TimePriceObjectBox_.time, timePrices.first().time, timePrices.last().time)
                                .equal(TimePriceObjectBox_.base, base)
                                .equal(TimePriceObjectBox_.target, target)
                                .build()
                                .find()
                                .map { Pair(it.time, it) }
                                .toMap()

                timePrices.forEach { timePrice ->
                    val existing = existingPrices[timePrice.time]

                    if (existingPrices[timePrice.time] == null || existing?.exchange != timePrice.exchange) {
                        timePriceBox.put(timePrice.toObjectBox(updateCategory))
                    }
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    enum class TimePriceUpdateCategory(val updateCategory: Long) {
        ExchangeRate(0),
        Day(1),
        Hour(2),
        Min(3)
    }

    fun readLastDay(base: CurrencyType, target: CurrencyType): UnixMilliSeconds {
        val milliInYear = milliInDay * 365
        return getLatestTime(base, target, TimePriceUpdateCategory.Day, milliInYear)
    }

    fun readLastHour(base: CurrencyType, target: CurrencyType): UnixMilliSeconds {
        val milliInWeek = milliInDay * 7
        return getLatestTime(base, target, TimePriceUpdateCategory.Hour, milliInWeek)
    }

    fun readLastMinute(base: CurrencyType, target: CurrencyType): UnixMilliSeconds {
        return getLatestTime(base, target, TimePriceUpdateCategory.Min, milliInDay)
    }

    private fun getLatestTime(base: CurrencyType, target: CurrencyType, updateCategory: TimePriceUpdateCategory, timeFromEarliest: UnixMilliSeconds): UnixMilliSeconds {
        val timePriceBox = box.boxFor<TimePriceObjectBox>()

        val currencyTypeConverter = CurrencyTypeConverter()

        val earliestPossible = System.currentTimeMillis() - timeFromEarliest

        val latestTimePrice: TimePriceObjectBox? =
                timePriceBox
                        .query()
                        .equal(TimePriceObjectBox_.updateCategory, updateCategory.updateCategory)
                        .greater(TimePriceObjectBox_.time, earliestPossible)
                        .equal(TimePriceObjectBox_.base, currencyTypeConverter.convertToDatabaseValue(base))
                        .equal(TimePriceObjectBox_.target, currencyTypeConverter.convertToDatabaseValue(target))
                        .orderDesc(TimePriceObjectBox_.time)
                        .build()
                        .findFirst()

        return latestTimePrice?.time ?: earliestPossible
    }

    fun getCurrentExchangeRate(base: CurrencyType, target: CurrencyType): Observable<DataSource<TimePrice>> {
        val timePriceBox = box.boxFor<TimePriceObjectBox>()

        val currencyTypeConverter = CurrencyTypeConverter()

        val query = timePriceBox
                .query()
                .equal(TimePriceObjectBox_.base, currencyTypeConverter.convertToDatabaseValue(base))
                .equal(TimePriceObjectBox_.target, currencyTypeConverter.convertToDatabaseValue(target))
                .orderDesc(TimePriceObjectBox_.time)
                .build()

        return RxQuery.observable(query)
                .map {
                    it.firstOrNull()?.fromObjectBox()?.run {
                        DataSource.Success(this)
                    } ?: DataSource.Error("Could not get price in past")
                }
    }

    fun getExchangeRateAtTime(
            base: CurrencyType,
            target: CurrencyType,
            milliSeconds: UnixMilliSeconds,
            exchangeType: ExchangeType
    ): Single<DataSource<TimePrice>> {
        val timePriceBox = box.boxFor<TimePriceObjectBox>()

        val currencyTypeConverter = CurrencyTypeConverter()
        val exchangeTypeConverter = ExchangeTypeConverter()

        val query = timePriceBox
                .query()
                .equal(TimePriceObjectBox_.time, milliSeconds)
                .equal(TimePriceObjectBox_.base, currencyTypeConverter.convertToDatabaseValue(base))
                .equal(TimePriceObjectBox_.target, currencyTypeConverter.convertToDatabaseValue(target))
                .equal(TimePriceObjectBox_.exchange, exchangeTypeConverter.convertToDatabaseValue(exchangeType))
                .build()

        return RxQuery.single(query)
                .map {
                    it.firstOrNull()?.fromObjectBox()?.run {
                        DataSource.Success(this)
                    } ?: DataSource.Error("Could not get price in past")
                }
    }

    private fun TimePrice.toObjectBox(timePriceUpdateCategory: TimePriceUpdateCategory): TimePriceObjectBox {
        return TimePriceObjectBox(
                time = this.time,
                base = this.base,
                target = this.target,
                price = this.price,
                exchange = this.exchange,
                updateCategory = timePriceUpdateCategory.updateCategory
        )
    }

    private fun TimePriceObjectBox.fromObjectBox(): TimePrice {
        return object : TimePrice {
            override val time: UnixMilliSeconds = this@fromObjectBox.time
            override val base: CurrencyType = this@fromObjectBox.base
            override val target: CurrencyType = this@fromObjectBox.target
            override val price: CurrencyAmount = this@fromObjectBox.price
            override val exchange: ExchangeType = this@fromObjectBox.exchange
        }
    }
}
