package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.*
import javax.inject.Inject

class CurrencyAmountUseCase @Inject constructor(private val sortTransactionUseCase: SortTransactionUseCase) {

    fun getCurrencyAmount(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return sortTransactionUseCase.getSortedTransactions(currencyType = currencyType)
                .map {
                    it.mapSuccess { transactions ->
                        val purchased = transactions
                                .purchased
                                .fold(0.0) { acc, transaction ->
                                    acc + transaction.toAmount
                                }

                        val sold = transactions
                                .sold
                                .fold(0.0) { acc, transaction ->
                                    acc + transaction.fromAmount
                                }

                        purchased - sold
                    }
                }
    }
}