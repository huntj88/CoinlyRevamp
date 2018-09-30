package me.jameshunt.repo.db.domain

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import me.jameshunt.base.*
import me.jameshunt.repo.db.CurrencyTypeConverter
import me.jameshunt.repo.db.ExchangeTypeConverter
import me.jameshunt.repo.db.TransactionStatusConverter

@Entity
data class TransactionObjectBox(
        @Id
        var id: Long = 0,

        @Index
        override val transactionId: TransactionId,

        @Convert(converter = CurrencyTypeConverter::class, dbType = Long::class)
        override val fromCurrencyType: CurrencyType,
        override val fromAmount: CurrencyAmount,

        @Convert(converter = CurrencyTypeConverter::class, dbType = Long::class)
        override val toCurrencyType: CurrencyType,
        override val toAmount: CurrencyAmount,
        override val time: UnixMilliSeconds,

        @Convert(converter = TransactionStatusConverter::class, dbType = Long::class)
        override val status: TransactionStatus,

        @Convert(converter = ExchangeTypeConverter::class, dbType = Long::class)
        override val exchangeType: ExchangeType

) : Transaction
