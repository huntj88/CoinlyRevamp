package me.jameshunt.appbase

import me.jameshunt.base.ActivityScope
import timber.log.Timber
import java.net.URI
import javax.inject.Inject

sealed class IntegrationDeepLink {
    data class Coinbase(val code: String) : IntegrationDeepLink()
}

@ActivityScope
class IntegrationDeepLinkHandler @Inject constructor() {
    var deepLink: IntegrationDeepLink? = null

    inline fun <reified T : IntegrationDeepLink> consumeDeepLinkData(): T? {
        val result = deepLink?.let { it as? T }
        deepLink = null

        return result
    }

    fun handleIntent(intentString: String) {
        if (!intentString.contains("huntj88://me.jameshunt.coinly")) return

        Timber.i(intentString)
        val uri = URI.create(intentString)

        when (uri.path) {
            "/coinbase" -> {
                val code = Regex("[a-zA-Z]*=([a-zA-Z0-9]*)").findAll(uri.query).map {
                    it.groups[1]?.value
                }.first()!!

                deepLink = IntegrationDeepLink.Coinbase(code = code)
            }
            else -> throw NotImplementedError()
        }
    }
}