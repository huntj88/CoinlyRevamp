package me.jameshunt.coinbase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.IntegrationStatus
import me.jameshunt.base.KeyValueTool
import me.jameshunt.base.Transaction

const val coinbaseAccessToken = "coinbaseAccessToken"
const val coinbaseRefreshToken = "coinbaseRefreshToken"
const val coinbaseMostRecentTransactionId = "coinbaseMostRecentTransactionId"

class CoinbaseIntegration(private val keyValueTool: KeyValueTool) {
    companion object {
        fun getAuthUrl() = "https://www.coinbase.com/oauth/authorize?" +
                "response_type=code&" +
                "client_id=${CoinbaseKeys.coinbaseClientId}&" +
                "redirect_uri=$redirectURI&" +
                "scope=$scopes&" +
                "account=all"
    }

    private val service by lazy { CoinbaseService() }

    fun getIntegrationStatus(): IntegrationStatus {
        return keyValueTool.get(coinbaseAccessToken)?.run { IntegrationStatus.Integrated }
                ?: IntegrationStatus.NotIntegrated
    }

    fun integrate(code: String): Completable {
        return service
                .exchangeCodeForToken(code = code)
                .doOnSuccess {
                    keyValueTool.set(coinbaseAccessToken, it.accessToken)
                    keyValueTool.set(coinbaseRefreshToken, it.refreshToken)
                }
                .toCompletable()
                .observeOn(Schedulers.io())
    }

    fun getTransactions(): Single<List<Transaction>> {

        // todo: handle refreshing credentials

        return applyAccessToken()
                .andThen(service.getTransactionsForCoin(
                        currencyType = CurrencyType.ETH,
                        mostRecent = keyValueTool.get(coinbaseMostRecentTransactionId)
                ).doOnSuccess { newTransactions ->
                    newTransactions.lastOrNull()?.let {
                        keyValueTool.set(coinbaseMostRecentTransactionId, it.transactionId)
                    }
                })
    }

    private fun applyAccessToken(): Completable {
        return Completable.fromAction {
            keyValueTool.get(coinbaseAccessToken)?.let {
                service.setAccessToken(it)
            } ?: throw IllegalStateException("coinbase credentials don't exist")
        }
    }
}
