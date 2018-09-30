package me.jameshunt.coinbase

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import me.jameshunt.base.Transaction
import me.jameshunt.base.TransactionId

class CoinbaseBox(context: Any) {

    private val box = MyObjectBox.builder().androidContext(context).name("coinbase").build()

    fun writeCredentials(tokenResponse: TokenResponse) {
        val credentialsBox = box.boxFor(CredentialsObjectBox::class.java)

        box.runInTx {
            credentialsBox.removeAll()
            credentialsBox.put(tokenResponse.toObjectBox())
        }
    }

    internal fun getCredentials(): CredentialsObjectBox? {
        return box.boxFor(CredentialsObjectBox::class.java).all.firstOrNull()
    }

    private fun TokenResponse.toObjectBox(): CredentialsObjectBox {
        return CredentialsObjectBox(accessToken = this.accessToken, refreshToken = this.refreshToken)
    }

    fun writeMostRecentTransactionId(transaction: Transaction) {
        val mostRecentBox = box.boxFor(MostRecentTransaction::class.java)

        box.runInTx {
            mostRecentBox.removeAll()
            mostRecentBox.put(MostRecentTransaction(transactionId = transaction.transactionId))
        }
    }

    internal fun getMostRecentTransactionId(): TransactionId? {
        return box.boxFor(MostRecentTransaction::class.java).all.firstOrNull()?.transactionId
    }
}

@Entity
data class CredentialsObjectBox(

        @Id
        var id: Long = 0,
        val accessToken: String = "",
        val refreshToken: String = ""
)

@Entity
data class MostRecentTransaction(
        @Id
        var id: Long = 0,
        val transactionId: TransactionId
)