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

    fun getExchangeRateAtTime(target: CurrencyType, unixMilliSeconds: UnixMilliSeconds, exchangeType: ExchangeType): Single<DataSource<Double>> {
        //fixed for certain exchanges
        val baseFixed = when(exchangeType) {
            ExchangeType.COINBASE -> target
            else -> selectedCurrencyUseCase.selectedBase
        }

        val targetFixed = when(exchangeType) {
            ExchangeType.COINBASE -> selectedCurrencyUseCase.selectedBase
            else -> target
        }

        return repository.getExchangeRateAtTime(baseFixed, targetFixed, unixMilliSeconds, exchangeType).map { exchangeRate ->
            exchangeRate.mapSuccess {
                when(exchangeType) {
                    ExchangeType.COINBASE -> it.price
                    else -> 1.0 / it.price
                }
            }
        }
    }
}