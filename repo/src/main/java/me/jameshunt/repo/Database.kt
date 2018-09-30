package me.jameshunt.repo

import io.objectbox.kotlin.boxFor
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.*
import me.jameshunt.repo.db.domain.*

internal class Database(context: Any) {

    private val box = MyObjectBox.builder().androidContext(context).build()

    fun writeTimePrice(timePrices: List<TimePrice>, updateCategory: TimePriceUpdateCategory): Completable {
        return Completable.fromAction {
            val timePriceBox = box.boxFor<TimePriceObjectBox>()

            box.runInTx {
                val existingPrices =
                        timePriceBox
                                .query()
                                .between(TimePriceObjectBox_.time, timePrices.first().time, timePrices.last().time)
                                .equal(TimePriceObjectBox_.base, timePrices.first().base.id)
                                .equal(TimePriceObjectBox_.other, timePrices.first().other.id)
                                .build()
                                .find()
                                .map { Pair(it.time, it) }
                                .toMap()

                timePrices.forEach { timePrice ->
                    if (existingPrices[timePrice.time] == null) {
                        timePriceBox.put(timePrice.toObjectBox(updateCategory))
                    }
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    fun writeTransactions(transactions: List<Transaction>): Completable {
        return Completable.fromAction {
            val transactionBox = box.boxFor(TransactionObjectBox::class.java)

            box.runInTx {
                transactions
                        .map { it.toObjectBox() }
                        .forEach { transactionBox.put(it) }
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun Transaction.toObjectBox(): TransactionObjectBox {
        return TransactionObjectBox(
                transactionId = this.transactionId,
                fromCurrencyType = this.fromCurrencyType,
                fromAmount = this.fromAmount,
                toCurrencyType = this.toCurrencyType,
                toAmount = this.toAmount,
                time = this.time,
                status = this.status,
                exchangeType = this.exchangeType
        )
    }

    private fun TimePrice.toObjectBox(timePriceUpdateCategory: TimePriceUpdateCategory): TimePriceObjectBox {
        return TimePriceObjectBox(
                time = this.time,
                base = this.base,
                other = this.other,
                price = this.price,
                updateCategory = timePriceUpdateCategory.updateCategory
        )
    }

    enum class TimePriceUpdateCategory(val updateCategory: Long) {
        CurrentPrice(0),
        Day(1),
        Hour(2),
        Min(3)
    }

    fun readLastDay(): UnixMilliSeconds {
        val milliInYear = milliInDay * 365
        return getLatestTime(TimePriceUpdateCategory.Day, milliInYear)
    }

    fun readLastHour(): UnixMilliSeconds {
        val milliInWeek = milliInDay * 7
        return getLatestTime(TimePriceUpdateCategory.Hour, milliInWeek)
    }

    fun readLastMinute(): UnixMilliSeconds {
        return getLatestTime(TimePriceUpdateCategory.Min, milliInDay)
    }

    private fun getLatestTime(updateCategory: TimePriceUpdateCategory, earliestPossible: UnixMilliSeconds): UnixMilliSeconds {
        val timePriceBox = box.boxFor<TimePriceObjectBox>()

        val latestTimePrice: TimePriceObjectBox? =
                timePriceBox
                        .query()
                        .equal(TimePriceObjectBox_.updateCategory, updateCategory.updateCategory)
                        .greater(TimePriceObjectBox_.time, earliestPossible)
                        .orderDesc(TimePriceObjectBox_.time)
                        .build()
                        .findFirst()

        return latestTimePrice?.time ?: System.currentTimeMillis()-earliestPossible
    }
}
