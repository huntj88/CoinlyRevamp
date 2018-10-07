package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.CurrencyType
import javax.inject.Inject

class SelectedCurrencyUseCase @Inject constructor() {

    fun getSelectedBase(): Observable<CurrencyType> = Observable.just(CurrencyType.USD)
    fun getSelectedTarget(): Observable<CurrencyType> = Observable.just(CurrencyType.ETH)
}