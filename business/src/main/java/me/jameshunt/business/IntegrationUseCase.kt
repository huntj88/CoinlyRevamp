package me.jameshunt.business

import dagger.Lazy
import io.reactivex.Completable
import me.jameshunt.coinbase.CoinbaseIntegration
import javax.inject.Inject

class IntegrationUseCase @Inject constructor(private val coinbaseIntegration: Lazy<CoinbaseIntegration>) {

    fun integrateCoinbase(code: String): Completable = coinbaseIntegration.get().integrate(code)

}