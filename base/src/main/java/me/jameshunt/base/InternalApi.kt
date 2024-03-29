package me.jameshunt.base

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.operators.observable.ObservableFromArray
import io.reactivex.internal.operators.observable.ObservableFromIterable
import java.lang.IllegalStateException

sealed class DataSource<out Type> {
    data class Success<Data>(val data: Data) : DataSource<Data>()
    data class Error(val message: String) : DataSource<Nothing>()
}

inline fun <A, B, C> DataSource<A>.join(other: DataSource<B>, joinOperation: (a: A, b: B) -> C): DataSource<C> {
    return when {
        this is DataSource.Error -> this
        other is DataSource.Error -> other
        this is DataSource.Success && other is DataSource.Success -> DataSource.Success(joinOperation(this.data, other.data))
        else -> throw IllegalStateException()
    }
}

inline fun <T, R> DataSource<T>.mapSuccess(transform: (T) -> R): DataSource<R> {
    return when (this) {
        is DataSource.Success -> DataSource.Success(transform(this.data))
        is DataSource.Error -> this
    }
}

inline fun <T> DataSource<T>.doOnError(doOnError: () -> Unit): DataSource<T> {
    return when (this) {
        is DataSource.Success -> this
        is DataSource.Error -> {
            doOnError()
            this
        }
    }
}

fun <T: DataSource<R>, R> DataSource<T>.flatten(): DataSource<R> {
    return when (this) {
        is DataSource.Success -> this.data
        is DataSource.Error -> this
    }
}

fun DataSource<String>.output(): String {
    return when (this) {
        is DataSource.Success -> this.data
        is DataSource.Error -> this.message
    }
}

fun DataSource<Double>.outputDouble(): String {
    return when (this) {
        is DataSource.Success -> this.data.toString()
        is DataSource.Error -> this.message
    }
}

/**
 * These Messages are more like a completable of individual an task.
 * This allows you to chain them together into a stream of multiple completed tasks.
 *
 * This allows you to get status updates of each task as it happens
 *
 * These streams can be composed of target Message streams if you have sub-tasks for each task
 *
 * To add a new task it should be added as a Single<Message> using the helper functions
 *
 * It is also acceptable to add two Streams together Via an Observable<Message>,
 * but only if the target Observable was created with a set of Single<Message>
 *
 * You can have the "tree" of streams go as deep as needed
 */

sealed class Message(internal var kickOffNext: Boolean = true) {
    data class Success(val message: String? = null) : Message()
    data class Error(val message: String) : Message()
}

fun Observable<Message>.passMessageThenNext(next: Observable<Message>): Observable<Message> =
        this.checkSource().concatMap { it.handle(next) }

fun Observable<Message>.passMessageThenNext(next: Single<Message>): Observable<Message> =
        this.checkSource().concatMap { it.handle(next.toObservable()) }

fun Single<Message>.passMessageThenNext(next: Observable<Message>): Observable<Message> =
        this.toObservable().concatMap { it.handle(next) }

fun Single<Message>.passMessageThenNext(next: Single<Message>): Observable<Message> =
        this.toObservable().concatMap { it.handle(next.toObservable()) }


fun Observable<Message>.passMessageThenNextEvenIfError(next: Observable<Message>): Observable<Message> =
        this.checkSource().concatMap { it.kickOffNextIfNecessary(next) }

fun Observable<Message>.passMessageThenNextEvenIfError(next: Single<Message>): Observable<Message> =
        this.checkSource().concatMap { it.kickOffNextIfNecessary(next.toObservable()) }

fun Single<Message>.passMessageThenNextEvenIfError(next: Observable<Message>): Observable<Message> =
        this.toObservable().concatMap { it.kickOffNextIfNecessary(next) }

fun Single<Message>.passMessageThenNextEvenIfError(next: Single<Message>): Observable<Message> =
        this.toObservable().concatMap { it.kickOffNextIfNecessary(next.toObservable()) }

private fun Observable<Message>.checkSource(): Observable<Message> {
    // non exhaustive, probably more edge cases to guard against
    // only something someone who hasn't read the docs at the top would worry about
    val message = "message stream should be build from singles or target message streams"
    if (this is ObservableFromArray) throw IllegalStateException(message)
    if (this is ObservableFromIterable) throw IllegalStateException(message)
    return this
}

private fun Message.handle(next: Observable<Message>): Observable<Message> {
    return when (this) {
        is Message.Success -> this.kickOffNextIfNecessary(next)
        is Message.Error -> this.kickOffNextIfNecessary(null)
    }
}

private fun Message.kickOffNextIfNecessary(next: Observable<Message>?): Observable<Message> {
    return when (this.kickOffNext && next != null) {
        true -> {
            this.kickOffNext = false
            Observable.concat(Observable.just(this), next)
        }
        false -> Observable.just(this)
    }
}