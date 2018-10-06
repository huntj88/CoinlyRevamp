package me.jameshunt.business

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import me.jameshunt.base.*
import javax.inject.Inject


class UpdateEverythingUseCase @Inject constructor(
        private val integrationUseCase: IntegrationUseCase,
        private val repository: Repository,
        private val enabledCoinsUseCase: EnabledCoinsUseCase,
        private val selectedCoinUseCase: SelectedCoinUseCase
) {

    fun updateEverything(): Observable<Message> {
        return Observable.just(Message.Success("Updating data") as Message)
                .passMessageThenNextEvenIfError(integrationUseCase.updateCoinbase())
                .passMessageThenNextEvenIfError(updateCurrentPrices())
                .passMessageThenNextEvenIfError(updateTimeRanges())

    }

    private fun updateCurrentPrices(): Single<Message> {
        return repository.updateCurrentPrices(
                base = CurrencyType.USD,
                others = setOf(CurrencyType.BTC, CurrencyType.ETH, CurrencyType.LTC, CurrencyType.BCH)
        )
    }

    private fun updateTimeRanges(): Observable<Message> {
        return Observables
                .combineLatest(
                        selectedCoinUseCase.getSelectedBase(),
                        enabledCoinsUseCase.getEnabledCoins()) { base, enabled -> Pair(base, enabled) }
                .flatMap { coinInfo ->
                    coinInfo.second
                            .asSequence()
                            .map { repository.updateTimeRanges(coinInfo.first, it) }
                            .fold(Observable.just(Message.Success() as Message)) { acc, observable ->
                                acc.passMessageThenNextEvenIfError(observable)
                            }
                }
    }
}