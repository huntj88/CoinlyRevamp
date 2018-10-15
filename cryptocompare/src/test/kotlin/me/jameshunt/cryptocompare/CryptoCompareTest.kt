package me.jameshunt.cryptocompare

import me.jameshunt.base.CurrencyType
import me.jameshunt.base.DataSource
import me.jameshunt.base.ExchangeType
import org.junit.Test

class CryptoCompareTest : BaseTester() {

    @Test
    fun getCurrentPrices() {
        CryptoCompare()
                .getCurrentPrices(CurrencyType.USD, setOf(CurrencyType.ETH, CurrencyType.BTC))
                .doOnSuccess {
                    when (it) {
                        is DataSource.Success -> it.data.printTimePrices()
                        is DataSource.Error -> it.printError()
                    }
                }
                .testSingle()
    }

    @Test
    fun getHistoricalPrice() {
        CryptoCompare()
                .getHistoricalPrices(CurrencyType.USD, setOf(CurrencyType.ETH, CurrencyType.BTC), time = 1537206634471, exchange = ExchangeType.NONE)
                .doOnSuccess { printResults(it) }
                .testSingle()
    }

    @Test
    fun getDailyPrices() {
        CryptoCompare()
                .getDailyPrices(CurrencyType.USD, CurrencyType.ETH, 20)
                .doOnSuccess { printResults(it) }
                .testSingle()
    }

    @Test
    fun getHourlyPrices() {
        CryptoCompare()
                .getHourlyPrices(CurrencyType.USD, CurrencyType.ETH, 20)
                .doOnSuccess { printResults(it) }
                .testSingle()
    }

    @Test
    fun getMinutePrices() {
        CryptoCompare()
                .getMinutePrices(CurrencyType.USD, CurrencyType.ETH, 20)
                .doOnSuccess { printResults(it) }
                .testSingle()
    }

    private fun <T> printResults(results: DataSource<T>) {
        when (results) {
            is DataSource.Success -> results.data.toString()
            is DataSource.Error -> results.printError()
        }
    }
}
