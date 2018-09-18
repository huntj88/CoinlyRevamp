package me.jameshunt.cryptocompare

import io.reactivex.Single

abstract class BaseTester {

    init {
        Environment.isTesting = true
    }

    fun <T : Any> Single<T>.testSingle() {
        this.test().assertNoErrors().assertComplete()
    }
}