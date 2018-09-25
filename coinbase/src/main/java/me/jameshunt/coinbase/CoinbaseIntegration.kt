package me.jameshunt.coinbase

import io.reactivex.Completable

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
        return service.exchangeCodeForToken(code = code).flatMapCompletable { coinbaseBox.writeCredentials(it) }
    }

    fun updateTransactions() {

    }
}
