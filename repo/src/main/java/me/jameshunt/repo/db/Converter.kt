package me.jameshunt.repo.db

import io.objectbox.converter.PropertyConverter
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.ExchangeType
import me.jameshunt.base.TransactionStatus

internal class CurrencyTypeConverter : PropertyConverter<CurrencyType, Long> {
    override fun convertToDatabaseValue(entityProperty: CurrencyType): Long {
        return entityProperty.id
    }

    override fun convertToEntityProperty(databaseValue: Long): CurrencyType {
        return CurrencyType.values().first { it.id == databaseValue }
    }
}

internal class ExchangeTypeConverter : PropertyConverter<ExchangeType, Long> {
    override fun convertToDatabaseValue(entityProperty: ExchangeType): Long {
        return entityProperty.id
    }

    override fun convertToEntityProperty(databaseValue: Long): ExchangeType {
        return ExchangeType.values().first { it.id == databaseValue }
    }
}

internal class TransactionStatusConverter : PropertyConverter<TransactionStatus, Long> {
    override fun convertToDatabaseValue(entityProperty: TransactionStatus): Long {
        return entityProperty.id
    }

    override fun convertToEntityProperty(databaseValue: Long): TransactionStatus {
        return TransactionStatus.values().first { it.id == databaseValue }
    }
}
