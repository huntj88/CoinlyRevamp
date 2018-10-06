package me.jameshunt.base

import io.reactivex.Observable
import io.reactivex.Single

sealed class DataSource<out Type> {
    data class Success<Data>(val data: Data) : DataSource<Data>()
    data class Error(val message: String) : DataSource<Nothing>()
}

sealed class Message(var kickOffNext: Boolean = true) {
    data class Success(val message: String? = null) : Message()
    data class Error(val message: String) : Message()
}

fun Observable<Message>.passMessageThenNext(next: Observable<Message>): Observable<Message> {
    return this.concatMap {
        when (it) {
            is Message.Success -> it.kickOfNextIfNecessary(next)
            is Message.Error -> it.kickOfNextIfNecessary(null)
        }
    }
}

fun Observable<Message>.passMessageThenNext(next: Single<Message>): Observable<Message> {
    return this.concatMap {
        when (it) {
            is Message.Success -> it.kickOfNextIfNecessary(next.toObservable())
            is Message.Error -> it.kickOfNextIfNecessary(null)
        }
    }
}

fun Single<Message>.passMessageThenNext(next: Observable<Message>): Observable<Message> {
    return this.toObservable().concatMap {
        when (it) {
            is Message.Success -> it.kickOfNextIfNecessary(next)
            is Message.Error -> it.kickOfNextIfNecessary(null)
        }
    }
}

fun Single<Message>.passMessageThenNext(next: Single<Message>): Observable<Message> {
    return this.toObservable().concatMap {
        when (it) {
            is Message.Success -> it.kickOfNextIfNecessary(next.toObservable())
            is Message.Error -> it.kickOfNextIfNecessary(null)
        }
    }
}


fun Observable<Message>.passMessageThenNextEvenIfError(next: Observable<Message>): Observable<Message> {
    return this.concatMap { it.kickOfNextIfNecessary(next) }
}

fun Observable<Message>.passMessageThenNextEvenIfError(next: Single<Message>): Observable<Message> {
    return this.concatMap { it.kickOfNextIfNecessary(next.toObservable()) }
}

private fun Message.kickOfNextIfNecessary(next: Observable<Message>?): Observable<Message> {
    return when (this.kickOffNext && next != null) {
        true -> {
            this.kickOffNext = false
            Observable.concat(Observable.just(this), next)
        }
        false -> Observable.just(this)
    }
}