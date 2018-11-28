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
            exchangeRate.mapSuccess { it.price }
        }
    }

    fun getExchangeRateAtTime(target: CurrencyType, unixMilliSeconds: UnixMilliSeconds, exchangeType: ExchangeType): Single<DataSource<Double>> {
        return repository.getExchangeRateAtTime(
                base = selectedCurrencyUseCase.selectedBase,
                target = target,
                milliSeconds = unixMilliSeconds,
                exchangeType = exchangeType
        ).map { exchangeRate ->
            exchangeRate.mapSuccess { it.price }
        }
    }
}