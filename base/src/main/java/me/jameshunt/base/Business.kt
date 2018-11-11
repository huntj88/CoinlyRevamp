package me.jameshunt.base

import io.reactivex.Observable

// is activity scoped and need to make available to lower scopes

interface SelectedCurrencyUseCase {

    var selectedBase: CurrencyType

    fun getSelectedTarget(): Observable<CurrencyType>
    fun setSelectedTarget(currencyType: CurrencyType)
}

interface SelectedTimeTypeUseCase {
    fun getSelectedTimeType(): Observable<TimeType>
    fun setSelectedTimeType(timeType: TimeType)
}