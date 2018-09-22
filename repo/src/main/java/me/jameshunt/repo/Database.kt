package me.jameshunt.repo

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.converter.PropertyConverter
import io.objectbox.kotlin.boxFor
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.CurrencyAmount
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.TimePrice
import me.jameshunt.base.UnixMilliSeconds

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
                                .equal(TimePriceObjectBox_.base, timePrices.first().base.name)
                                .equal(TimePriceObjectBox_.other, timePrices.first().other.name)
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

    private fun TimePrice.toObjectBox(timePriceUpdateCategory: TimePriceUpdateCategory): TimePriceObjectBox {
        return TimePriceObjectBox(
                time = time,
                base = base,
                other = other,
                price = price,
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

@Entity
data class TimePriceObjectBox(
        @Id
        var id: Long = 0,

        @Index
        override val time: UnixMilliSeconds,

        @Convert(converter = CurrencyTypeConverter::class, dbType = String::class)
        override val base: CurrencyType,

        @Convert(converter = CurrencyTypeConverter::class, dbType = String::class)
        override val other: CurrencyType,

        override val price: CurrencyAmount,

        @Index
        val updateCategory: Long
) : TimePrice

internal class CurrencyTypeConverter : PropertyConverter<CurrencyType, String> {
    override fun convertToDatabaseValue(entityProperty: CurrencyType): String {
        return entityProperty.name
    }

    override fun convertToEntityProperty(databaseValue: String): CurrencyType {
        return CurrencyType.valueOf(databaseValue)
    }
}