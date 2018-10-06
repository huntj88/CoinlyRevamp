package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.CurrencyType
import javax.inject.Inject

class EnabledCoinsUseCase @Inject constructor() {
    fun getEnabledCoins(): Observable<Set<CurrencyType>> =
            Observable.just(setOf(CurrencyType.BTC, CurrencyType.ETH, CurrencyType.LTC, CurrencyType.BCH))
}