package me.jameshunt.repo

import io.objectbox.BoxStore
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.DataSource
import me.jameshunt.base.Transaction
import me.jameshunt.repo.db.CurrencyTypeConverter
import me.jameshunt.repo.db.domain.TransactionObjectBox
import me.jameshunt.repo.db.domain.TransactionObjectBox_

class TransactionDatabase(private val box: BoxStore) {

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

    fun readTransactions(currencyType: CurrencyType): Observable<DataSource<List<Transaction>>> {
        val transactionBox = box.boxFor(TransactionObjectBox::class.java)
        val currencyTypeConverter = CurrencyTypeConverter()

        val currencyTypeId = currencyTypeConverter.convertToDatabaseValue(currencyType)

        val query = transactionBox.query()
                .equal(TransactionObjectBox_.toCurrencyType, currencyTypeId)
                .or()
                .equal(TransactionObjectBox_.fromCurrencyType, currencyTypeId)
                .build()

        return RxQuery.observable(query)
                .map { transactions -> transactions.map { it.fromObjectBox() } }
                .map { DataSource.Success(it) }
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

    private fun TransactionObjectBox.fromObjectBox(): Transaction {
        return object : Transaction {
            override val transactionId = this@fromObjectBox.transactionId
            override val fromCurrencyType = this@fromObjectBox.fromCurrencyType
            override val fromAmount = this@fromObjectBox.fromAmount
            override val toCurrencyType = this@fromObjectBox.toCurrencyType
            override val toAmount = this@fromObjectBox.toAmount
            override val time = this@fromObjectBox.time
            override val status = this@fromObjectBox.status
            override val exchangeType = this@fromObjectBox.exchangeType
        }
    }
}