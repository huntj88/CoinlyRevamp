package me.jameshunt.base

import io.reactivex.Observable

interface SelectedCurrencyUseCase {

    fun getSelectedBase(): Observable<CurrencyType>
    fun setSelectedBase(coinType: CurrencyType)

    fun getSelectedTarget(): Observable<CurrencyType>
    fun setSelectedTarget(currencyType: CurrencyType)
}