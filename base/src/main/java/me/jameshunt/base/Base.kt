package me.jameshunt.base

typealias UnixMilliSeconds = Long
typealias CurrencyAmount = Double

enum class TimeType {
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR
}

enum class CurrencyType(val fullName: String) {
    UNSUPPORTED("Unsupported"),

    // Fiat
    USD("Dollar"),
    EUR("Euro"),

    // Crypto
    BTC("Bitcoin"),
    ETH("Ethereum"),
    LTC("Litecoin"),
    BCH("Bitcoin Cash"),
    FUN("Funfair"),
    XLM("Stellar"),
    ADA("Cardano"),
    NEO("NEO"),
    VEN("VeChain"),
    IOTA("Iota"),
    BAT("Basic Attention Token");
}

interface TimePrice {
    val time: UnixMilliSeconds
    val base: CurrencyType
    val other: CurrencyType
    val price: CurrencyAmount
}
