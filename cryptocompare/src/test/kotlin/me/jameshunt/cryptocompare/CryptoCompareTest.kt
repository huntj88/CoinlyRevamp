package me.jameshunt.cryptocompare

import me.jameshunt.base.CurrencyType
import org.junit.Test

class CryptoCompareTest: BaseTester() {

    @Test
    fun getCurrentPrices() {
        CryptoCompare()
                .getCurrentPrices(CurrencyType.USD, setOf(CurrencyType.ETH, CurrencyType.BTC))
                .doOnSuccess { print(it) }
                .testSingle()
    }

    @Test
    fun getHistoricalPrice() {
        CryptoCompare()
                .getHistoricalPrices(CurrencyType.USD, CurrencyType.ETH, time = 1537206634471)
                .doOnSuccess { print(it) }
                .testSingle()
    }
}