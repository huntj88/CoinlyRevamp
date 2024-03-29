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

                        val purchasedAmount = getPurchaseAmount(sortedTransactions.purchased)
                        val soldAmount = getSoldAmount(sortedTransactions.sold)

                        purchasedAmount.join(soldAmount) { purchased, sold -> purchased - sold }
                    }.flatten()
                }
    }

    private fun getPurchaseAmount(purchases: List<Transaction>): DataSource<Double> {
        return purchases.asSequence()
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
                }.foldDoubles()
    }

    private fun getSoldAmount(sales: List<Transaction>): DataSource<Double> {
        return sales.asSequence()
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
                }.foldDoubles()
    }

    private fun Sequence<DataSource<Double>>.foldDoubles(): DataSource<Double> {
        return this.fold(DataSource.Success(0.0) as DataSource<Double>) { acc, dataSource ->
            acc.join(dataSource) { a, b -> a + b }
        }
    }
}