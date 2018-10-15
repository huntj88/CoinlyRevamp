package me.jameshunt.business

import io.reactivex.Observable
import io.reactivex.Single
import me.jameshunt.base.*
import me.jameshunt.base.SelectedCurrencyUseCase
import javax.inject.Inject


class UpdateEverythingUseCase @Inject constructor(
        private val integrationUseCase: IntegrationUseCase,
        private val repository: Repository,
        private val enabledCurrencyUseCase: EnabledCurrencyUseCase,
        private val selectedCurrencyUseCase: SelectedCurrencyUseCase
) {

    fun updateEverything(): Observable<Message> {
        return integrationUseCase.updateCoinbase().toObservable()
                .mergeWith(updateExchangeRates().toObservable())
                .mergeWith(updateTimeRanges())
    }

    private fun updateExchangeRates(): Single<Message> {
        return repository.updateExchangeRates(
                base = selectedCurrencyUseCase.selectedBase,
                targets = enabledCurrencyUseCase.getEnabledCurrencies().blockingFirst()
        )
    }

    private fun updateTimeRanges(): Observable<Message> {
        val base = selectedCurrencyUseCase.selectedBase

        return enabledCurrencyUseCase.getEnabledCurrencies()
                .flatMap {
                    it.asSequence()
                            .map { target -> repository.updateTimeRanges(base, target) }
                            .fold(Observable.just(Message.Success("Updating Time Ranges") as Message)) { acc, observable ->
                                acc.passMessageThenNextEvenIfError(observable)
                            }
                }
    }
}