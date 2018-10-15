package me.jameshunt.business

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import me.jameshunt.base.*
import javax.inject.Inject

class GainUseCase @Inject constructor(
        private val valueUseCase: ValueUseCase,
        private val paidUseCase: PaidUseCase
) {

    fun getUnrealizedGain(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return Observables.combineLatest(valueUseCase.getValue(currencyType), paidUseCase.getPaidForCurrentlyHeld(currencyType)) { value, paid ->
            value.join(paid) { v, p -> v - p }
        }
    }

//    fun getRealizedGains(currencyType: CurrencyType): Observable<DataSource<Double>> {
//        return sortTransactionUseCase.getSortedTransactions(currencyType).map {
//            it.mapSuccess { sorted ->
//                val sold = Stack<Transaction>().apply {
//                    sorted.sold.reversed().forEach { sale -> this.push(sale) }
//                }
//
//                val purchased = Stack<Transaction>().apply {
//                    sorted.purchased.reversed().forEach { purchase -> this.push(purchase) }
//                }
//
//                var baseCurrency = if (purchased.peek() != null) purchased.pop().toAmount else 0.0
//
//                while (sold.isNotEmpty()) {
//
//                    if (baseCurrency > 0) {
//                        val sale = sold.pop()
//                        baseCurrency -= convertToBase(sale.toCurrencyType, sale.toAmount
//                    }
//
//                    if (baseCurrency <= 0.0) {
//                        baseCurrency += if (purchased.peek() != null) purchased.pop().toAmount else 0.0
//                    }
//                }
//
//            }
//        }.map { }
//    }
//
//    private fun convertToBase(other: CurrencyType, amount: Double, unixMilliSeconds: UnixMilliSeconds): Double {
//        //todo: convert selling to other currencies to the base. like if selling ETH for XLM
//        // currently base is always in USD, so just return amount
//
//        return when (other) {
//            CurrencyType.USD -> amount //todo replace hard coded USD with the selected base currency
//            else -> throw IllegalStateException()
//        }
//    }
}