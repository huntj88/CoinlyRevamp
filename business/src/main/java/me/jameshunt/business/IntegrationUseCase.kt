package me.jameshunt.business

import dagger.Module
import dagger.Provides
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

    fun integrateCoinbase(code: String): Observable<Message> {
        return coinbaseIntegration
                .integrate(code)
                .passMessageThenNext(Single.defer { updateCoinbase() })
    }

    fun updateCoinbase(): Single<Message> {
        return when (coinbaseIntegrationStatus) {
            IntegrationStatus.NotIntegrated -> Single.just(Message.Error("Coinbase not integrated"))
            IntegrationStatus.Integrated -> {
                coinbaseIntegration
                        .getTransactions()
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
    }
}

@Module
class CoinbaseModule {

    @Provides
    fun getCoinbaseIntegration(keyValueTool: KeyValueTool): CoinbaseIntegration = CoinbaseIntegration(keyValueTool)
}