package dev.entao.base

import java.util.*


fun <T : Any> Stack<T>.top(): T? {
    return this.lastOrNull()
}

fun <T : Any> Stack<T>.popOrNull(): T? {
    return this.removeLastOrNull()
}

