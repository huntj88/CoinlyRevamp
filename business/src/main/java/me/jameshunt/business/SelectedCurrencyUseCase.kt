package me.jameshunt.business

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.SelectedCurrencyUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedCurrencyUseCaseImpl @Inject constructor(): SelectedCurrencyUseCase {

    private lateinit var emitterBase: ObservableEmitter<CurrencyType>
    private val observableBase = Observable
            .create<CurrencyType> {
                emitterBase = it
                emitterBase.onNext(CurrencyType.USD)
            }
            .replay(1)
            .autoConnect()

    override fun getSelectedBase(): Observable<CurrencyType> = observableBase

    override fun setSelectedBase(coinType: CurrencyType) {
        emitterBase.onNext(coinType)
    }



    private lateinit var emitterTarget: ObservableEmitter<CurrencyType>
    private val observableTarget = Observable
            .create<CurrencyType> {
                emitterTarget = it
                emitterTarget.onNext(CurrencyType.ETH)
            }
            .replay(1)
            .autoConnect()

    override fun getSelectedTarget(): Observable<CurrencyType> = observableTarget

    override fun setSelectedTarget(currencyType: CurrencyType) {
        emitterTarget.onNext(currencyType)
    }

    init {
        println(getSelectedBase().blockingFirst())
        println(getSelectedTarget().blockingFirst())
    }
}