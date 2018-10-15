package me.jameshunt.base

import io.reactivex.Observable

interface SelectedCurrencyUseCase {

    var selectedBase: CurrencyType

    fun getSelectedTarget(): Observable<CurrencyType>
    fun setSelectedTarget(currencyType: CurrencyType)
}