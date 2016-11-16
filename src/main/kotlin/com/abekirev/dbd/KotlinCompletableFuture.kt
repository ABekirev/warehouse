package com.abekirev.dbd

import java.util.concurrent.CompletableFuture

fun <T> CompletableFuture<T>.kcf() = KotlinCompletableFuture(this)

class KotlinCompletableFuture<T>(private val cf: CompletableFuture<T>) {
    fun <R> thenApply(fn: (T) -> R): KotlinCompletableFuture<R> {
        return KotlinCompletableFuture(cf.thenApply(fn))
    }

    fun <R> thenApplyAsync(fn: (T) -> R): KotlinCompletableFuture<R> {
        return KotlinCompletableFuture(cf.thenApplyAsync(fn))
    }
}