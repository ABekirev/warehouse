package com.abekirev.dbd

import java.util.stream.Collectors
import java.util.stream.Stream

fun <T> Stream<T>.toList(): List<T> {
    return collect(Collectors.toList()) as List<T>
}

fun Int.isOdd(): Boolean = this % 2 == 0
