package me.jameshunt.business

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import me.jameshunt.base.*
import javax.inject.Inject

class ValueUseCase @Inject constructor(
        private val currencyAmountUseCase: CurrencyAmountUseCase,
        private val exchangeRateUseCase: ExchangeRateUseCase,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase
) {

    fun getValue(currencyType: CurrencyType): Observable<DataSource<Double>> {
        val currencyAmountObservable = currencyAmountUseCase.getCurrencyAmount(currencyType = currencyType)

        val exchangeRateObservable = selectedCurrencyUseCase
                .getSelectedBase()
                .flatMap {
                    exchangeRateUseCase.getCurrentExchangeRate(base = it, target = currencyType)
                }

        return Observables.combineLatest(currencyAmountObservable, exchangeRateObservable) { currencyAmount, exchangeRate ->
            currencyAmount.join(exchangeRate) { amount, exchange ->
                amount * exchange
            }
        }
    }
}