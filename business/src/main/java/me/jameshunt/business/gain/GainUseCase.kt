package me.jameshunt.business.gain

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import me.jameshunt.base.*
import me.jameshunt.business.PaidUseCase
import me.jameshunt.business.ValueUseCase
import javax.inject.Inject

class GainUseCase @Inject constructor(
        private val valueUseCase: ValueUseCase,
        private val paidUseCase: PaidUseCase,
        private val realizedGainUseCase: RealizedGainUseCase
) {

    fun getUnrealizedGain(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return Observables.combineLatest(valueUseCase.getValue(currencyType), paidUseCase.getPaidForCurrentlyHeld(currencyType)) { value, paid ->
            value.join(paid) { v, p -> v - p }
        }
    }

    fun getRealizedGain(currencyType: CurrencyType): Observable<DataSource<Double>> {
        return realizedGainUseCase.getRealizedGain(currencyType)
    }

    fun getNetProfit(currencyType: CurrencyType): Observable<DataSource<Double>> {
        val combinedObservable = Observables.combineLatest(
                this.getUnrealizedGain(currencyType),
                realizedGainUseCase.getRealizedGain(currencyType))

        return combinedObservable.map  { (realized, unrealized) ->
            realized.join(unrealized) { a, b -> a + b }
        }
    }
}