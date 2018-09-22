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

class Database(context: Any) {

    private val box = MyObjectBox.builder().androidContext(context).build()

    fun writeTimePrice(timePrices: List<TimePrice>): Completable {
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
                        timePriceBox.put(timePrice.toObjectBox())
                    }
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun TimePrice.toObjectBox(): TimePriceObjectBox {
        return TimePriceObjectBox(
                time = time,
                base = base,
                other = other,
                price = price
        )
    }

    fun readLastDay(): UnixMilliSeconds = 1537388014000
    fun readLastHour(): UnixMilliSeconds = 1537480014000
    fun readLastMinute(): UnixMilliSeconds = 1537488014000
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

        override val price: CurrencyAmount
) : TimePrice

class CurrencyTypeConverter : PropertyConverter<CurrencyType, String> {
    override fun convertToDatabaseValue(entityProperty: CurrencyType): String {
        return entityProperty.name
    }

    override fun convertToEntityProperty(databaseValue: String): CurrencyType {
        return CurrencyType.valueOf(databaseValue)
    }
}