package me.jameshunt.coinbase

class Integration {
    private val redirectURI = "huntj88://me.jameshunt.coinly/coinbase"
    private val scopes = "wallet:accounts:read,wallet:transactions:read"

    fun getAuthUrl() = "https://www.coinbase.com/oauth/authorize?" +
            "response_type=code&" +
            "client_id=${CoinbaseKeys.coinbaseClientId}&" +
            "redirect_uri=$redirectURI&" +
            "scope=$scopes&" +
            "account=all"
}