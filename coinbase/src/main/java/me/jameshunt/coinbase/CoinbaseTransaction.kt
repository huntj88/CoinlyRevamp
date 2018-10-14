package me.jameshunt.coinbase

import com.squareup.moshi.Json
import me.jameshunt.base.*
import org.threeten.bp.Instant
import kotlin.math.absoluteValue

data class CoinbaseTransaction(
        val pagination: Pagination,
        val data: List<Data>
)

data class Data(
        val id: TransactionId,
        val type: String,
        val status: String,
        val amount: Amount,

        @Json(name = "native_amount")
        val nativeAmount: NativeAmount,
        val description: String?,

        @Json(name = "created_at")
        val createdAt: String,

        @Json(name = "updated_at")
        val updatedAt: String,
        val resource: String,

        @Json(name = "resource_path")
        val resourcePath: String,
        val network: Network?,
        val buy: Buy?,
        val details: Details
) {

    fun getTransaction(): Transaction? {
        return if (!isTransfer()) {
            object : Transaction {
                override val transactionId: TransactionId = id
                override val fromCurrencyType: CurrencyType = getFromCurrencyType()
                override val fromAmount: Double = getFromAmount()
                override val toCurrencyType: CurrencyType = getToCurrencyType()
                override val toAmount: Double = getToAmount()
                override val time: UnixMilliSeconds = getUnixMilliSeconds()
                override val status: TransactionStatus = getTransactionStatus()
                override val exchangeType: ExchangeType = ExchangeType.COINBASE
            }
        } else null
    }

    fun getTransfer(): Transfer? {

        return if (isTransfer()) {
            network?.let {
                //todo: this wont work for internal transfers. see when ryan accidentally sent 0.38 litecoin to me
                if (it.amount != null && it.hash != null && it.fee != null) {

                    object : Transfer {
                        override val transferId: String = id
                        override val time: UnixMilliSeconds = getUnixMilliSeconds()
                        override val hash: String = it.hash
                        override val currencyType: CurrencyType = CurrencyType.valueOf(it.amount.currency)
                        override val amount: Double = it.amount.amount.toDouble()
                        override val fee: Double = it.fee.amount.toDouble()
                        override val type: TransferType = TransferType.SEND

                    }
                } else null
            }
        } else null
    }

    private fun getTransactionStatus(): TransactionStatus {
        return when (status) {
            "completed" -> TransactionStatus.COMPLETE
            "pending" -> TransactionStatus.PENDING
            else -> {
                TransactionStatus.UNSUPPORTED
            }
        }
    }

    private fun getUnixMilliSeconds(): UnixMilliSeconds = Instant.parse(createdAt).toEpochMilli()

    private fun getFromCurrencyType(): CurrencyType {
        return when (type) {
            "buy" -> CurrencyType.USD
            "sell" -> CurrencyType.valueOf(amount.currency)
            else -> CurrencyType.UNSUPPORTED
        }
    }

    private fun getToCurrencyType(): CurrencyType {
        return when (type) {
            "buy" -> CurrencyType.valueOf(amount.currency)
            "sell" -> CurrencyType.USD
            else -> CurrencyType.UNSUPPORTED
        }
    }

    private fun getFromAmount(): Double {
        return when (getFromCurrencyType()) {
            CurrencyType.USD -> nativeAmount.amount.toDouble()
            else -> amount.amount.toDouble()
        }
    }

    private fun getToAmount(): Double {
        return when (getToCurrencyType()) {
            CurrencyType.USD -> nativeAmount.amount.toDouble()
            else -> amount.amount.toDouble()
        }
    }

    fun isTransfer(): Boolean {
        return type == "send"
    }
}

data class Amount(
        val amount: String,
        val currency: String
)

data class NativeAmount(
        val amount: String,
        val currency: String
)

data class Buy(
        val id: String,
        val resource: String,
        val resourcePath: String?
)

data class Details(
        val title: String,
        val subtitle: String
)

data class Network(
        val hash: String?,

        @Json(name = "transaction_fee")
        val fee: Amount?,

        @Json(name = "transaction_amount")
        val amount: Amount?
)

data class Pagination(
        @Json(name = "ending_before")
        val endingBefore: String?,

        @Json(name = "starting_after")
        val startingAfter: String?,
        val limit: Int,
        val order: String,

        @Json(name = "previous_uri")
        val previousUri: String?,

        @Json(name = "next_uri")
        val nextUri: String?
)