package me.jameshunt.base

import io.reactivex.Observable
import io.reactivex.Single

sealed class DataSource<out Type> {
    data class Success<Data>(val data: Data) : DataSource<Data>()
    data class Error(val message: String) : DataSource<Nothing>()
}

sealed class Message(internal var kickOffNext: Boolean = true) {
    data class Success(val message: String? = null) : Message()
    data class Error(val message: String) : Message()
}

fun Observable<Message>.passMessageThenNext(next: Observable<Message>): Observable<Message> =
        this.concatMap { it.handle(next) }

fun Observable<Message>.passMessageThenNext(next: Single<Message>): Observable<Message> =
        this.concatMap { it.handle(next.toObservable()) }

fun Single<Message>.passMessageThenNext(next: Observable<Message>): Observable<Message> =
        this.toObservable().concatMap { it.handle(next) }

fun Single<Message>.passMessageThenNext(next: Single<Message>): Observable<Message> =
        this.toObservable().concatMap { it.handle(next.toObservable()) }



fun Observable<Message>.passMessageThenNextEvenIfError(next: Observable<Message>): Observable<Message> =
        this.concatMap { it.kickOfNextIfNecessary(next) }

fun Observable<Message>.passMessageThenNextEvenIfError(next: Single<Message>): Observable<Message> =
        this.concatMap { it.kickOfNextIfNecessary(next.toObservable()) }

fun Single<Message>.passMessageThenNextEvenIfError(next: Observable<Message>): Observable<Message> =
        this.toObservable().concatMap { it.kickOfNextIfNecessary(next) }

fun Single<Message>.passMessageThenNextEvenIfError(next: Single<Message>): Observable<Message> =
        this.toObservable().concatMap { it.kickOfNextIfNecessary(next.toObservable()) }


private fun Message.handle(next: Observable<Message>): Observable<Message> {
    return when (this) {
        is Message.Success -> this.kickOfNextIfNecessary(next)
        is Message.Error -> this.kickOfNextIfNecessary(null)
    }
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