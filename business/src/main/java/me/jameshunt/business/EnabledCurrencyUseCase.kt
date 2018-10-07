package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.CurrencyType
import javax.inject.Inject

class EnabledCurrencyUseCase @Inject constructor() {
    fun getEnabledCurrencies(): Observable<Set<CurrencyType>> =
            Observable.just(setOf(CurrencyType.BTC, CurrencyType.ETH, CurrencyType.LTC, CurrencyType.BCH))
}