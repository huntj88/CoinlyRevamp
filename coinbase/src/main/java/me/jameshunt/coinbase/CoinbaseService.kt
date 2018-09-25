package me.jameshunt.coinbase

import io.reactivex.Single

class CoinbaseService {
    private val client = ClientFactory().client

    fun exchangeCodeForToken(code: String): Single<TokenResponse> {
        return client.getTokens(code = code)
    }
}