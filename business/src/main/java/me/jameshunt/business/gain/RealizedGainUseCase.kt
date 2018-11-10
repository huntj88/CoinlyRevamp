package me.jameshunt.business.gain

import io.reactivex.Observable
import me.jameshunt.base.*
import me.jameshunt.business.ExchangeRateUseCase
import me.jameshunt.business.SortTransactionUseCase
import me.jameshunt.fifo.Fifo
import javax.inject.Inject

class RealizedGainUseCase @Inject constructor(
        private val sortTransactionUseCase: SortTransactionUseCase,
        private val exchangeRateUseCase: ExchangeRateUseCase,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase
) {

    internal fun getRealizedGain(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return sortTransactionUseCase.getSortedTransactions(currencyType = currencyType)
                .map { dataSource ->
                    dataSource.mapSuccess { (purchased, sold) ->

                        val fifoPurchases = purchased
                                .map { it.getFifoPurchaseTransaction() }
                                .joinIndividualTransaction()

                        val fifoSales = sold
                                .map { it.getFifoSaleTransaction() }
                                .joinIndividualTransaction()

                        fifoPurchases.join(fifoSales) { purchases, sales ->

                            purchases.printTotal()
                            sales.printTotal()

                            Fifo.findRealizedGain(purchases, sales)
                        }
                    }.flatten()
                }
    }

    /** TEMP: method for seeing numbers before they get fifo'd */
    private fun List<Fifo.Transaction>.printTotal() {
        if(this.isEmpty()) return
        this.reduce { acc, pair ->
            Fifo.Transaction(pair.items + acc.items, pair.currencyAmount + acc.currencyAmount)
        }.also { println("total:$it") }
    }

    private fun List<DataSource<Fifo.Transaction>>.joinIndividualTransaction(): DataSource<List<Fifo.Transaction>> {
        val foldInitial = DataSource.Success(
                listOf<Fifo.Transaction>()
        ) as DataSource<List<Fifo.Transaction>>

        return this.fold(foldInitial) { acc, dataSource ->
            acc.join(dataSource) { a, b -> a + listOf(b) }
        }
    }

    private fun Transaction.getFifoPurchaseTransaction(): DataSource<Fifo.Transaction> {
        return when (selectedCurrencyUseCase.selectedBase == this.fromCurrencyType) {
            true -> DataSource.Success(Fifo.Transaction(items = this.toAmount, currencyAmount = this.fromAmount))
            false -> exchangeRateUseCase
                    .getExchangeRateAtTime(
                            target = this.toCurrencyType,
                            unixMilliSeconds = this.time,
                            exchangeType = this.exchangeType
                    )
                    .blockingGet()
                    .mapSuccess { it * this.toAmount }
                    .mapSuccess { Fifo.Transaction(items = it, currencyAmount = this.toAmount) }
        }
    }

    private fun Transaction.getFifoSaleTransaction(): DataSource<Fifo.Transaction> {
        return when (selectedCurrencyUseCase.selectedBase == this.toCurrencyType) {
            true -> DataSource.Success(Fifo.Transaction(items = this.fromAmount, currencyAmount = this.toAmount))
            false -> exchangeRateUseCase
                    .getExchangeRateAtTime(
                            target = this.fromCurrencyType,
                            unixMilliSeconds = this.time,
                            exchangeType = this.exchangeType
                    )
                    .blockingGet()
                    .mapSuccess { it * this.fromAmount }
                    .mapSuccess { Fifo.Transaction(items = this.fromAmount, currencyAmount = it) }
        }
    }
}