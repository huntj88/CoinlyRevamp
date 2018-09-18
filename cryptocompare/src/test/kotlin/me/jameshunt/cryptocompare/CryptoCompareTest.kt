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
                .getHistoricalPrices(CurrencyType.USD, setOf(CurrencyType.ETH, CurrencyType.BTC), time = 1537206634471)
                .doOnSuccess { print(it) }
                .testSingle()
    }

    @Test
    fun getDailyPrices() {
        CryptoCompare()
                .getDailyPrices(CurrencyType.USD, CurrencyType.ETH, 20)
                .doOnSuccess { print(it) }
                .testSingle()
    }

    @Test
    fun getHourlyPrices() {
        CryptoCompare()
                .getHourlyPrices(CurrencyType.USD, CurrencyType.ETH, 20)
                .doOnSuccess { print(it) }
                .testSingle()
    }

    @Test
    fun getMinutePrices() {
        CryptoCompare()
                .getMinutePrices(CurrencyType.USD, CurrencyType.ETH, 20)
                .doOnSuccess { print(it) }
                .testSingle()
    }
}
