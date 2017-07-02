package com.togglecorp.paiso.promise

import android.util.Log

private typealias SuccessHandler<T> = (result: T?) -> Unit
private typealias ErrorHandler = (exception: Throwable?) -> Unit

class Promise<T> {

    enum class State { PENDING, FULFILLED, REJECTED }
    private var state = State.PENDING
    private var exception: Throwable? = null

    private var result: T? = null
    private var successHandlers: MutableList<SuccessHandler<T>> = mutableListOf()
    private var errorHandlers: MutableList<ErrorHandler> = mutableListOf()


    fun resolve(result: T?) {
        if (this.state != State.PENDING) {
            return
        }

        this.result = result
        this.state = State.FULFILLED

        successHandlers.forEach { it.invoke(this.result) }
    }

    fun reject(exception: Throwable?) {
        if (this.state != State.PENDING) {
            return
        }

        this.exception = exception
        this.state = State.REJECTED

        errorHandlers.forEach { it.invoke(this.exception) }
    }

    fun <T1> thenPromise(consumer: (value: T?) -> Promise<T1?>) : Promise<T1?> {
        when (this.state) {
            State.PENDING -> {
                val promise = Promise<T1?>()
                successHandlers.add({
                    consumer.invoke(it).then { promise.resolve(it) }
                            .catch { promise.reject(it) }
                })
                errorHandlers.add({
                    promise.reject(it)
                })
                return promise
            }
            State.FULFILLED -> {
                return consumer.invoke(this.result)
            }
            State.REJECTED -> {
                val promise = Promise<T1?>()
                promise.reject(this.exception)
                return promise
            }
        }
    }

    fun catchPromise(catcher: (exception: Throwable?) -> Promise<T?>) : Promise<T?> {
        when (this.state) {
            State.PENDING -> {
                val promise = Promise<T?>()
                successHandlers.add({
                    promise.resolve(it)
                })
                errorHandlers.add({
                    catcher.invoke(it).then { promise.resolve(it) }
                            .catch { promise.reject(it) }
                })
                return promise
            }
            State.FULFILLED -> {
                val promise = Promise<T?>()
                promise.resolve(this.result)
                return promise
            }
            State.REJECTED -> {
                return catcher.invoke(this.exception)
            }
        }
    }

    fun <T1> then(consumer: (value: T?) -> T1?) : Promise<T1?> {
        when (this.state) {
            State.PENDING -> {
                val promise = Promise<T1?>()
                successHandlers.add({
                    promise.resolve(consumer.invoke(it))
                })
                errorHandlers.add({
                    promise.reject(it)
                })
                return promise
            }
            State.FULFILLED -> {
                val promise = Promise<T1?>()
                promise.resolve(consumer.invoke(this.result))
                return promise
            }
            State.REJECTED -> {
                val promise = Promise<T1?>()
                promise.reject(this.exception)
                return promise
            }
        }
    }

    fun catch(catcher: (exception: Throwable?) -> T?) : Promise<T?> {
        when (this.state) {
            State.PENDING -> {
                val promise = Promise<T?>()
                successHandlers.add({
                    promise.resolve(it)
                })
                errorHandlers.add({
                    promise.resolve(catcher.invoke(it))
                })
                return promise
            }
            State.FULFILLED -> {
                val promise = Promise<T?>()
                promise.resolve(this.result)
                return promise
            }
            State.REJECTED -> {
                val promise = Promise<T?>()
                promise.resolve(catcher.invoke(this.exception))
                return promise
            }
        }
    }

    companion object {
        fun <T> all(promises: List<Promise<T?>>) : Promise<List<T?>> {
            val promise = Promise<List<T?>>()
            val results = mutableListOf<T?>()

            if (promises.isEmpty()) {
                promise.resolve(results)
            } else {
                var completed = 0;
                val total = promises.size
                promises.forEach {
                    it.then {
                        synchronized(completed, {
                            results.add(it)
                            completed++
                            if (total == completed) {
                                promise.resolve(results)
                            }
                        })
                    }

                    it.catch {
                        synchronized(completed, {
                            promise.reject(it)
                            null
                        })
                    }
                }
            }

            return promise
        }

        fun <T> all(vararg promises: Promise<T?>) : Promise<List<T?>> = all(promises.asList())

        fun <T> race(promises: List<Promise<T?>>) : Promise<T?> {
            var resolved = false
            val promise = Promise<T?>()

            if (promises.isEmpty()) {
                promise.resolve(null)
            } else {
                promises.forEach {
                    it.then {
                        synchronized(resolved, {
                            if (!resolved) {
                                resolved = true
                                promise.resolve(it)
                            }
                        })
                    }

                    it.catch {
                        synchronized(resolved, {
                            promise.reject(it)
                            null
                        })
                    }
                }
            }

            return promise
        }

        fun <T> race(vararg promises: Promise<T?>) : Promise<T?> = race(promises.asList())
    }
}

