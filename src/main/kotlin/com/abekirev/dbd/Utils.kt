package com.abekirev.dbd

import one.util.streamex.StreamEx
import java.util.stream.Collectors
import java.util.stream.Stream

fun <T> Stream<T>.toList(): List<T> {
    return collect(Collectors.toList()) as List<T>
}

fun Int.isOdd(): Boolean = this % 2 == 0

fun <T> Iterable<T>.streamEx(): StreamEx<T> {
    return StreamEx.of(iterator())
}