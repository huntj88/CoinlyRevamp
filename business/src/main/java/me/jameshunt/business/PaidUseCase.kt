package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.*
import javax.inject.Inject

class PaidUseCase @Inject constructor(
        private val sortTransactionUseCase: SortTransactionUseCase,
        private val exchangeRateUseCase: ExchangeRateUseCase,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase
) {

    fun getPaidForCurrentlyHeld(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return sortTransactionUseCase.getSortedTransactions(currencyType = currencyType)
                .map { dataSource ->
                    dataSource.mapSuccess { sortedTransactions ->

                        val purchasedAmount = sortedTransactions.purchased
                                .asSequence()
                                .map { purchase ->
                                    when (selectedCurrencyUseCase.selectedBase == purchase.fromCurrencyType) {
                                        true -> DataSource.Success(purchase.fromAmount)
                                        false -> exchangeRateUseCase
                                                .getExchangeRateAtTime(
                                                        target = purchase.toCurrencyType,
                                                        unixMilliSeconds = purchase.time,
                                                        exchangeType = purchase.exchangeType
                                                )
                                                .blockingGet()
                                                .mapSuccess { it * purchase.toAmount }
                                    }
                                }
                                .fold(DataSource.Success(0.0) as DataSource<Double>) { acc, dataSource ->
                                    acc.join(dataSource) { a, b -> a + b }
                                }

                        val soldAmount = sortedTransactions.sold
                                .asSequence()
                                .map { sold ->
                                    when (selectedCurrencyUseCase.selectedBase == sold.toCurrencyType) {
                                        true -> DataSource.Success(sold.toAmount)
                                        false -> exchangeRateUseCase
                                                .getExchangeRateAtTime(
                                                        target = sold.fromCurrencyType,
                                                        unixMilliSeconds = sold.time,
                                                        exchangeType = sold.exchangeType
                                                )
                                                .blockingGet()
                                                .mapSuccess { it * sold.fromAmount }
                                    }
                                }
                                .fold(DataSource.Success(0.0) as DataSource<Double>) { acc, dataSource ->
                                    acc.join(dataSource) { a, b -> a + b }
                                }

                        purchasedAmount.join(soldAmount) { purchased, sold -> purchased - sold }
                    }.flatten()
                }
    }
}