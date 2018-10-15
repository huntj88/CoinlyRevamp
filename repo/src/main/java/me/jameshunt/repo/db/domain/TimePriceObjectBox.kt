package me.jameshunt.repo.db.domain

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import me.jameshunt.base.*
import me.jameshunt.repo.db.CurrencyTypeConverter
import me.jameshunt.repo.db.ExchangeTypeConverter

@Entity
data class TimePriceObjectBox(
        @Id
        var id: Long = 0,

        @Index
        override val time: UnixMilliSeconds,

        @Convert(converter = CurrencyTypeConverter::class, dbType = Long::class)
        override val base: CurrencyType,

        @Convert(converter = CurrencyTypeConverter::class, dbType = Long::class)
        override val target: CurrencyType,

        override val price: CurrencyAmount,

        @Convert(converter = ExchangeTypeConverter::class, dbType = Long::class)
        override val exchange: ExchangeType,

        @Index
        val updateCategory: Long
) : TimePrice