package me.jameshunt.repo.db.domain

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import me.jameshunt.base.CurrencyAmount
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.TimePrice
import me.jameshunt.base.UnixMilliSeconds
import me.jameshunt.repo.db.CurrencyTypeConverter

@Entity
data class TimePriceObjectBox(
        @Id
        var id: Long = 0,

        @Index
        override val time: UnixMilliSeconds,

        @Convert(converter = CurrencyTypeConverter::class, dbType = Long::class)
        override val base: CurrencyType,

        @Convert(converter = CurrencyTypeConverter::class, dbType = Long::class)
        override val other: CurrencyType,

        override val price: CurrencyAmount,

        @Index
        val updateCategory: Long
) : TimePrice