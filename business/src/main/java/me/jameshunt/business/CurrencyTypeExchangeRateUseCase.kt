package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.DataSource
import me.jameshunt.base.Repository
import me.jameshunt.base.mapSuccess
import javax.inject.Inject

class CurrencyTypeExchangeRateUseCase @Inject constructor(private val repository: Repository) {

    fun getCurrentExchangeRate(base: CurrencyType, target: CurrencyType): Observable<DataSource<Double>> {
        return repository.getCurrentExchangeRate(base, target).map { exchangeRate ->
            exchangeRate.mapSuccess { it.price }
        }
    }

}