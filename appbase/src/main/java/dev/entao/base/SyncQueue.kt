@file:Suppress("MemberVisibilityCanBePrivate")

package dev.entao.base

import java.util.*

/**
 * Created by entaoyang@163.com on 16/6/17.
 */


class SyncQueue<T> {
    val queue = LinkedList<T>()

    val size: Int get() = queue.size

    fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    @Synchronized
    fun clear() {
        queue.clear()
    }

    @Synchronized
    fun add(e: T) {
        queue.add(e)
    }

    @Synchronized
    fun popFirst(): T? {
        return queue.pollFirst()
    }

    @Synchronized
    fun first(): T? {
        return queue.peekFirst()
    }
}