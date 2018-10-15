package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.*
import javax.inject.Inject

class PaidUseCase @Inject constructor(
        private val sortTransactionUseCase: SortTransactionUseCase,
        private val exchangeRateUseCase: ExchangeRateUseCase
) {

    // get paid for current portfolio
    fun getPaidForCurrentlyHeldOld(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return sortTransactionUseCase.getSortedTransactions(currencyType = currencyType)
                .map {
                    it.mapSuccess { sortedTransactions ->
                        val purchasedAmount = sortedTransactions.purchased
                                .asSequence()
                                .map { purchase -> convertToBase(purchase.fromCurrencyType, purchase.fromAmount, purchase.time) }
                                .fold(0.0) { acc, amount -> acc + amount }

                        val soldAmount = sortedTransactions.sold
                                .asSequence()
                                .map { sold -> convertToBase(sold.toCurrencyType, sold.toAmount, sold.time) }
                                .fold(0.0) { acc, amount -> acc + amount }

                        purchasedAmount - soldAmount
                    }
                }
    }

    //todo: use this one
    fun getPaidForCurrentlyHeld(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return sortTransactionUseCase.getSortedTransactions(currencyType = currencyType)
                .map {
                    it.mapSuccess { sortedTransactions ->
                        val purchasedAmount = sortedTransactions.purchased
                                .asSequence()
                                .map { purchase -> exchangeRateUseCase.getExchangeRateAtTime(purchase.toCurrencyType, purchase.time).blockingGet() }
                                .fold(DataSource.Success(0.0) as DataSource<Double>) { acc, dataSource ->
                                    acc.join(dataSource) { a, b -> a + b }
                                }

                        val soldAmount = sortedTransactions.sold
                                .asSequence()
                                .map { sold -> exchangeRateUseCase.getExchangeRateAtTime(sold.fromCurrencyType, sold.time).blockingGet() }
                                .fold(DataSource.Success(0.0) as DataSource<Double>) { acc, dataSource ->
                                    acc.join(dataSource) { a, b -> a + b }
                                }

                        purchasedAmount.join(soldAmount) { purchased, sold ->
                            purchased - sold }
                    }.flatten()
                }
    }

    private fun convertToBase(other: CurrencyType, amount: Double, unixMilliSeconds: UnixMilliSeconds): Double {
        //todo: convert selling to other currencies to the base. like if selling ETH for XLM
        // currently base is always in USD, so just return amount

        return when (other) {
            CurrencyType.USD -> amount //todo replace hard coded USD with the selected base currency
            else -> throw IllegalStateException()
        }
    }
}