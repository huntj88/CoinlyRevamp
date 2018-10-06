package me.jameshunt.base

import io.reactivex.Observable
import io.reactivex.Single

sealed class DataSource<out Type> {
    data class Success<Data>(val data: Data): DataSource<Data>()
    data class Error(val message: String): DataSource<Nothing>()
}

sealed class Message {
    data class Success(val message: String? = null): Message()
    data class Error(val message: String): Message()
}

fun Observable<Message>.passMessageThenNext(next: Observable<Message>): Observable<Message> {
    return this.flatMap {
        when (it) {
            is Message.Success -> Observable.concat(Observable.just(it), next)
            is Message.Error -> Observable.just(it)
        }
    }
}

fun Observable<Message>.passMessageThenNext(next: Single<Message>): Observable<Message> {
    return this.flatMap {
        when (it) {
            is Message.Success -> Observable.concat(Observable.just(it), next.toObservable())
            is Message.Error -> Observable.just(it)
        }
    }
}