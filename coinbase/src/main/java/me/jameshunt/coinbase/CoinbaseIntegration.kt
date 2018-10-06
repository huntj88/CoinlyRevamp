package me.jameshunt.coinbase

import io.reactivex.Single
import me.jameshunt.base.*

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

    fun integrate(code: String): Single<Message> {
        return service
                .exchangeCodeForToken(code = code)
                .doOnSuccess {
                    if (it is CoinbaseService.CoinbaseResponse.Success) {
                        keyValueTool.set(coinbaseAccessToken, it.data.accessToken)
                        keyValueTool.set(coinbaseRefreshToken, it.data.refreshToken)
                    }
                }
                .map {
                    when (it) {
                        is CoinbaseService.CoinbaseResponse.Success -> Message.Success("Successfully integrated Coinbase")
                        is CoinbaseService.CoinbaseResponse.Error -> Message.Error("Failed to integrate with Coinbase")
                        is CoinbaseService.CoinbaseResponse.PartialResults -> throw IllegalStateException()
                    }
                }
    }

    fun getTransactions(): Single<CoinbaseService.CoinbaseResponse<List<Transaction>>> {
        return service
                .getTransactionsForCoin(
                        currencyType = CurrencyType.ETH,
                        mostRecent = keyValueTool.get(coinbaseMostRecentTransactionId)
                )
                .doOnSuccess { response ->
                    when (response) {
                        is CoinbaseService.CoinbaseResponse.Success -> writeMostRecentId(response.data.lastOrNull())
                        is CoinbaseService.CoinbaseResponse.PartialResults -> writeMostRecentId(response.data.lastOrNull())
                    }
                }
    }

    private fun writeMostRecentId(lastTransaction: Transaction?) {
        lastTransaction?.let {
            keyValueTool.set(coinbaseMostRecentTransactionId, it.transactionId)
        }
    }
}
