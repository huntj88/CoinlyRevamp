package me.jameshunt.business

import io.reactivex.Observable
import io.reactivex.Single
import me.jameshunt.base.*
import javax.inject.Inject

class ExchangeRateUseCase @Inject constructor(
        private val repository: Repository,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase
) {

    fun getCurrentExchangeRate(base: CurrencyType, target: CurrencyType): Observable<DataSource<Double>> {
        return repository.getCurrentExchangeRate(base, target).map { exchangeRate ->
            exchangeRate.mapSuccess { 1.0 / it.price }
        }
    }

    fun getExchangeRateAtTime(target: CurrencyType, unixMilliSeconds: UnixMilliSeconds): Single<DataSource<Double>> {
        val base = selectedCurrencyUseCase.selectedBase

        return repository.getExchangeRateAtTime(base, target, unixMilliSeconds).map { exchangeRate ->
            exchangeRate.mapSuccess { 1.0 / it.price }
        }
    }
}