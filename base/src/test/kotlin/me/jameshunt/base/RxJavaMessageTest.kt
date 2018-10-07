package me.jameshunt.base

import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test

class RxJavaMessageTest {

    @Test
    fun test1() {

        val testDataOne = listOf("hello1", "hello1.1", "hello1.2", "hello1.3")
        val testDataTwo = listOf("hello2", "hello2.1") + testDataOne
        val testDataThree = testDataTwo + listOf("hello3")

        val correctOrder = testDataOne + testDataTwo + testDataThree

        println(correctOrder)

        getTestData1()
                .passMessageThenNext(getTestData2())
                .passMessageThenNext(getTestData3())
                .blockingIterable()
                .forEachIndexed { index, message ->
                    when (message) {
                        is Message.Success -> Assert.assertEquals(correctOrder[index], message.message)
                        is Message.Error -> Assert.assertTrue(false)
                    }
                }
    }

    private fun getTestData1(): Observable<Message> = Single
            .just(Message.Success("hello1") as Message)
            .passMessageThenNext(Single.just(Message.Success("hello1.1") as Message))
            .passMessageThenNext(Single.just(Message.Success("hello1.2") as Message))
            .passMessageThenNext(Single.just(Message.Success("hello1.3") as Message))

    private fun getTestData2(): Observable<Message> = Single
            .just(Message.Success("hello2") as Message)
            .passMessageThenNext(Single.just(Message.Success("hello2.1") as Message))
            .passMessageThenNext(getTestData1())

    private fun getTestData3(): Observable<Message> = getTestData2().passMessageThenNext(Single.just(Message.Success("hello3") as Message))
}