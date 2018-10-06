package me.jameshunt.cryptocompare

import io.reactivex.Single
import me.jameshunt.base.DataSource
import me.jameshunt.base.TimePrice

abstract class BaseTester {

    init {
        Environment.isTesting = true
    }

    fun <T : Any> Single<T>.testSingle() {
        this.test().assertNoErrors().assertComplete()
    }

    fun List<TimePrice>.printTimePrices() {
        this.forEach { it.print() }
    }

    fun DataSource.Error.printError() {
        println(this.message)
    }

    fun TimePrice.print() {
        println("time: $time - base: $base - other: $other - price: $price")
    }
}
