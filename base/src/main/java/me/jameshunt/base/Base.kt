package me.jameshunt.base

typealias UnixMilliSeconds = Long
typealias CurrencyAmount = Double
typealias TransactionId = String

enum class TimeType {
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR
}

enum class CurrencyType(val id: Long, val fullName: String) {
    UNSUPPORTED(-1, "Unsupported"),

    USD(0, "Dollar"),
    EUR(1, "Euro"),


    BTC(1000, "Bitcoin"),
    ETH(1001, "Ethereum"),
    LTC(1002, "Litecoin"),
    BCH(1003, "Bitcoin Cash"),
    FUN(1004, "Funfair"),
    XLM(1005, "Stellar"),
    ADA(1006, "Cardano"),
    NEO(1007, "NEO"),
    VEN(1008, "VeChain"),
    IOTA(1009, "Iota"),
    BAT(1010, "Basic Attention Token");
}

enum class TransactionStatus(val id: Long) {
    UNSUPPORTED(-1),
    COMPLETE(0),
    PENDING(1);
}

enum class TransferType(val id: Long) {
    SEND(0),
    RECEIVED(1);
}

enum class ExchangeType(val id: Long) {
    NONE(0),
    COINBASE(1),
    //BINANCE(2);
}


interface TimePrice {
    val time: UnixMilliSeconds
    val base: CurrencyType
    val other: CurrencyType
    val price: CurrencyAmount

    fun string(): String = "time: $time - base: $base - other: $other - price: $price"
}

interface Transaction {
    val transactionId: TransactionId
    val fromCurrencyType: CurrencyType
    val fromAmount: Double
    val toCurrencyType: CurrencyType
    val toAmount: Double
    val time: UnixMilliSeconds
    val status: TransactionStatus
    val exchangeType: ExchangeType
}

interface Transfer {
    val transferId: String
    val time: UnixMilliSeconds
    val hash: String
    val currencyType: CurrencyType
    val amount: Double
    val fee: Double
    val type: TransferType
}


data class ObjectBoxContext(val context: Any)