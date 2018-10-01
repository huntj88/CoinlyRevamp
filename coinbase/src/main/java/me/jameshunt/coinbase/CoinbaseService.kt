package me.jameshunt.coinbase

import io.reactivex.Single
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.KeyValueTool
import me.jameshunt.base.Transaction
import me.jameshunt.base.TransactionId

class CoinbaseService(keyValueTool: KeyValueTool) {
    private val clientManager = ClientManager(keyValueTool)
    private val client = clientManager.client

    fun exchangeCodeForToken(code: String): Single<TokenResponse> {
        return client.getTokensWithCode(code = code)
    }

    // todo: any errors in the stream will cause the whole thing to cancel and throw out already collected responses
    fun getTransactionsForCoin(currencyType: CurrencyType, mostRecent: TransactionId? = null, previous: List<Transaction> = listOf()): Single<List<Transaction>> {
        return getOnePageOfTransactions(currencyType, mostRecent)
                .flatMap {
                    val retrievedTransactions = previous + it.transactions
                    when (it.isLast) {
                        true -> Single.just(retrievedTransactions)
                        false -> getTransactionsForCoin(
                                currencyType = currencyType,
                                mostRecent = it.transactions.last().transactionId,
                                previous = retrievedTransactions)
                    }
                }
    }

    private fun getOnePageOfTransactions(currencyType: CurrencyType, mostRecent: TransactionId?): Single<TransactionWrapper> {
        return client.getTransactionsForCoin(currencyType = currencyType, recentTransactionID = mostRecent)
                .map { coinbaseResponse ->
                    val transactions = coinbaseResponse.data
                            .asSequence()
                            .filter { !it.isTransfer() }
                            .map { it.getTransaction()!! }
                            .toList()

                    val isLast = coinbaseResponse.pagination.nextUri == null

                    TransactionWrapper(transactions, isLast)
                }
    }

    private data class TransactionWrapper(
            val transactions: List<Transaction>,
            val isLast: Boolean
    )
}