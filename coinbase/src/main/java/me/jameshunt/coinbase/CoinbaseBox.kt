package me.jameshunt.coinbase

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.reactivex.Completable

class CoinbaseBox(context: Any) {

    private val box = MyObjectBox.builder().androidContext(context).name("coinbase").build()

    fun writeCredentials(tokenResponse: TokenResponse): Completable {

        return Completable.fromAction {
            val credentialsBox = box.boxFor(CredentialsObjectBox::class.java)

            box.runInTx {
                credentialsBox.removeAll()
                credentialsBox.put(tokenResponse.toObjectBox())
            }
        }
    }

    internal fun getCredentials(): CredentialsObjectBox? {
        return box.boxFor(CredentialsObjectBox::class.java).all.firstOrNull()
    }

    private fun TokenResponse.toObjectBox(): CredentialsObjectBox {
        return CredentialsObjectBox(accessToken = this.accessToken, refreshToken = this.refreshToken)
    }
}

@Entity
data class CredentialsObjectBox(

        @Id
        var id: Long = 0,
        val accessToken: String = "",
        val refreshToken: String = ""
)