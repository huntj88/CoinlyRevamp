package me.jameshunt.coinbase

import io.reactivex.Completable
import io.reactivex.Single
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.Transaction
import java.lang.IllegalStateException

class CoinbaseIntegration(context: Any) {
    companion object {
        fun getAuthUrl() = "https://www.coinbase.com/oauth/authorize?" +
                "response_type=code&" +
                "client_id=${CoinbaseKeys.coinbaseClientId}&" +
                "redirect_uri=$redirectURI&" +
                "scope=$scopes&" +
                "account=all"
    }

    private val coinbaseBox = CoinbaseBox(context)
    private val service = CoinbaseService()

    fun integrate(code: String): Completable {
        return service
                .exchangeCodeForToken(code = code)
                .doOnSuccess { coinbaseBox.writeCredentials(tokenResponse = it) }
                .toCompletable()
    }

    fun getTransactions(): Single<List<Transaction>> {

        // todo: handle refreshing credentials

        coinbaseBox.getCredentials()?.let {
            service.setAccessToken(it.accessToken)
        } ?: throw IllegalStateException("coinbase credentials don't exist")

        return service
                .getTransactionsForCoin(
                        currencyType = CurrencyType.ETH,
                        mostRecent = coinbaseBox.getMostRecentTransactionId())
                .doOnSuccess { newTransactions ->
                    newTransactions.lastOrNull()?.let {
                        coinbaseBox.writeMostRecentTransactionId(it)
                    }
                }
    }
}
