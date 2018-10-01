package me.jameshunt.coinbase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.IntegrationStatus
import me.jameshunt.base.KeyValueTool
import me.jameshunt.base.Transaction

internal const val coinbaseAccessToken = "coinbaseAccessToken"
internal const val coinbaseRefreshToken = "coinbaseRefreshToken"
internal const val coinbaseMostRecentTransactionId = "coinbaseMostRecentTransactionId"

class CoinbaseIntegration(private val keyValueTool: KeyValueTool) {
    companion object {
        fun getAuthUrl() = "https://www.coinbase.com/oauth/authorize?" +
                "response_type=code&" +
                "client_id=${CoinbaseKeys.coinbaseClientId}&" +
                "redirect_uri=$redirectURI&" +
                "scope=$scopes&" +
                "account=all"
    }

    private val service by lazy { CoinbaseService(keyValueTool) }

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
        return service
                .getTransactionsForCoin(
                        currencyType = CurrencyType.ETH,
                        mostRecent = keyValueTool.get(coinbaseMostRecentTransactionId)
                )
                .doOnSuccess { newTransactions ->
                    newTransactions.lastOrNull()?.let {
                        keyValueTool.set(coinbaseMostRecentTransactionId, it.transactionId)
                    }
                }
    }
}
