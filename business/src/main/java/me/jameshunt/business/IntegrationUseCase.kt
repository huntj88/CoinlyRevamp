package me.jameshunt.business

import io.reactivex.Completable
import me.jameshunt.base.IntegrationStatus
import me.jameshunt.base.Repository
import me.jameshunt.coinbase.CoinbaseIntegration
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

}