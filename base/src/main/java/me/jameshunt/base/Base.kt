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

interface TimePrice {
    val time: UnixMilliSeconds
    val base: CurrencyType
    val other: CurrencyType
    val price: CurrencyAmount

    fun string(): String = "time: $time - base: $base - other: $other - price: $price"
}
