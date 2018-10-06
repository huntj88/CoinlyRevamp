package me.jameshunt.business

import dagger.Module
import dagger.Provides
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import me.jameshunt.base.*
import me.jameshunt.coinbase.CoinbaseIntegration
import me.jameshunt.coinbase.CoinbaseService
import javax.inject.Inject

class IntegrationUseCase @Inject constructor(
        private val repo: Repository,
        private val coinbaseIntegration: CoinbaseIntegration
) {

    val coinbaseIntegrationStatus: IntegrationStatus
        get() = coinbaseIntegration.getIntegrationStatus()

    fun integrateCoinbase(code: String): Completable {
        return coinbaseIntegration
                .integrate(code)
                .andThen(coinbaseIntegration.getTransactions())
                .flatMapCompletable { repo.writeTransactions(it) }
    }

    fun integrateCoinbase2(code: String): Observable<Message> {
        return coinbaseIntegration
                .integrate2(code)
                .toObservable()
                .passMessageThenNext(updateCoinbase2())
    }

    fun updateCoinbase(): Completable {
        return coinbaseIntegration.getTransactions().flatMapCompletable { repo.writeTransactions(it) }
    }

    fun updateCoinbase2(): Single<Message> {
        return coinbaseIntegration
                .getTransactions2()
                .flatMap {
                    when (it) {
                        is CoinbaseService.CoinbaseResponse.Success -> repo
                                .writeTransactions(it.data)
                                .toSingle { Message.Success("Successfully updated Coinbase, ${it.data.size} new transactions") }

                        is CoinbaseService.CoinbaseResponse.PartialResults -> repo
                                .writeTransactions(it.data)
                                .toSingle {
                                    Message.Error("Partial results: Updated ${it.data.size} transactions, with error: ${it.error.message}")
                                }
                        is CoinbaseService.CoinbaseResponse.Error -> Single.just(Message.Error(it.message))
                    }
                }
    }
}

@Module
class CoinbaseModule {

    @Provides
    fun getCoinbaseIntegration(keyValueTool: KeyValueTool): CoinbaseIntegration = CoinbaseIntegration(keyValueTool)
}