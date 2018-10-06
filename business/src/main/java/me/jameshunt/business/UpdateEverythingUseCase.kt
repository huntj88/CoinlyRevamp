package me.jameshunt.business

import io.reactivex.Observable
import me.jameshunt.base.IntegrationStatus
import me.jameshunt.base.Message
import me.jameshunt.base.Repository
import me.jameshunt.base.passMessageThenNext
import javax.inject.Inject

class UpdateEverythingUseCase @Inject constructor(
        private val integrationUseCase: IntegrationUseCase,
        private val repository: Repository
) {

    fun updateEverything(): Observable<Message> {
        // todo: also update data from cryptoCompare

        var observable: Observable<Message> = Observable.just(Message.Success("Updating data"))

        if(integrationUseCase.coinbaseIntegrationStatus == IntegrationStatus.Integrated) {
            observable = observable.passMessageThenNext(integrationUseCase.updateCoinbase())
        }

        return observable
    }

//    private fun testRepo() {
//        repo
//                .updateTimeRanges(CurrencyType.ETH, CurrencyType.USD)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                        onError = { it.printStackTrace() },
//                        onComplete = { Timber.i("time ranges updated") }
//                )
//
//        repo.updateCurrentPrices(CurrencyType.USD, setOf(CurrencyType.BTC, CurrencyType.ETH))
//                .subscribeBy(
//                        onError = { it.printStackTrace() },
//                        onComplete = { Timber.i("current prices updated") }
//                )
//    }
}