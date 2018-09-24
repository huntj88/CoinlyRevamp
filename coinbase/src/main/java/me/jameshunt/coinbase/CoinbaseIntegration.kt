package me.jameshunt.coinbase

class CoinbaseIntegration {
    companion object {
        private const val redirectURI = "huntj88://me.jameshunt.coinly/coinbase"
        private const val scopes = "wallet:accounts:read,wallet:transactions:read"

        fun getAuthUrl() = "https://www.coinbase.com/oauth/authorize?" +
                "response_type=code&" +
                "client_id=${CoinbaseKeys.coinbaseClientId}&" +
                "redirect_uri=$redirectURI&" +
                "scope=$scopes&" +
                "account=all"
    }
}