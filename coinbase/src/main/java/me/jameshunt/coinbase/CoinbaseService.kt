package me.jameshunt.coinbase

import io.reactivex.Single
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.KeyValueTool
import me.jameshunt.base.Transaction
import me.jameshunt.base.TransactionId
import retrofit2.HttpException
import java.lang.IllegalStateException

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


    fun getTransactionsForCoin2(currencyType: CurrencyType, mostRecent: TransactionId? = null, previous: List<Transaction> = listOf()): Single<CoinbaseResponse<List<Transaction>>> {
        return getOnePageOfTransactions2(currencyType, mostRecent)
                .flatMap {
                    when (it) {
                        is CoinbaseResponse.Success -> handleSuccess2(currencyType, previous, it)
                        is CoinbaseResponse.PartialResults -> throw IllegalStateException("not possible here")
                        is CoinbaseResponse.Error -> {
                            when(previous.isEmpty()) {
                                true -> Single.just(it)
                                false -> Single.just(CoinbaseResponse.PartialResults(previous, it))
                            }
                        }
                    }
                }
    }

    private fun handleSuccess2(
            currencyType: CurrencyType,
            previous: List<Transaction>,
            response: CoinbaseResponse.Success<TransactionWrapper>): Single<CoinbaseResponse<List<Transaction>>> {

        val data = response.data
        val retrievedTransactions = previous + data.transactions

        return when (data.isLast) {
            true -> Single.just(CoinbaseResponse.Success(retrievedTransactions))
            false -> getTransactionsForCoin2(
                    currencyType = currencyType,
                    mostRecent = data.transactions.last().transactionId,
                    previous = retrievedTransactions)
        }
    }

    private fun getOnePageOfTransactions2(currencyType: CurrencyType, mostRecent: TransactionId?): Single<CoinbaseResponse<TransactionWrapper>> {
        return client
                .getTransactionsForCoin(currencyType, mostRecent)
                .map { coinbaseResponse ->
                    val transactions = coinbaseResponse.data
                            .asSequence()
                            .filter { !it.isTransfer() }
                            .map { it.getTransaction()!! }
                            .toList()

                    val isLast = coinbaseResponse.pagination.nextUri == null

                    val wrapper = TransactionWrapper(transactions, isLast)
                    CoinbaseResponse.Success(wrapper) as CoinbaseResponse<TransactionWrapper>
                }
                .onErrorReturn {
                    when (it) {
                        is HttpException -> it.response().code().toError()
                        else -> CoinbaseResponse.Error.Unknown
                    }
                }
    }

    private fun Int.toError(): CoinbaseResponse.Error {
        return when (this) {
            400 -> CoinbaseResponse.Error.BadRequest
            401 -> CoinbaseResponse.Error.Unauthorized
            402 -> CoinbaseResponse.Error.TwoFactorAuthRequired
            403 -> CoinbaseResponse.Error.InvalidScope
            404 -> CoinbaseResponse.Error.NotFound
            429 -> CoinbaseResponse.Error.TooManyRequests
            500 -> CoinbaseResponse.Error.InternalServerError
            503 -> CoinbaseResponse.Error.ServiceUnavailable
            else -> CoinbaseResponse.Error.Unknown
        }
    }

    sealed class CoinbaseResponse<out Type> {
        data class Success<Data>(val data: Data) : CoinbaseResponse<Data>()
        data class PartialResults<Data>(val data: Data, val error: Error) : CoinbaseResponse<Data>()

        sealed class Error : CoinbaseResponse<Nothing>() {
            object BadRequest : Error()
            object Unauthorized : Error()
            object TwoFactorAuthRequired : Error()
            object InvalidScope : Error()
            object NotFound : Error()
            object TooManyRequests : Error()
            object InternalServerError : Error()
            object ServiceUnavailable : Error()
            object Unknown : Error() // general case
        }
    }

    private data class TransactionWrapper(
            val transactions: List<Transaction>,
            val isLast: Boolean
    )
}