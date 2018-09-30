package me.jameshunt.business

import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.Repository
import me.jameshunt.coinbase.CoinbaseIntegration
import javax.inject.Inject

class IntegrationUseCase @Inject constructor(
        private val repo: Repository,
        private val coinbaseIntegration: Lazy<CoinbaseIntegration>
) {

    fun integrateCoinbase(code: String): Completable {
        return coinbaseIntegration.get()
                .integrate(code)
                .observeOn(Schedulers.io())
                .andThen(coinbaseIntegration.get().getTransactions())
                .flatMapCompletable { repo.writeTransactions(it) }
    }

}