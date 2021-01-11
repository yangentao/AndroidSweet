package dev.entao.base

import java.util.*

/**
 * Created by yangentao on 2015/11/22.
 * entaoyang@163.com
 */
class FixedQueue<T>(max: Int) : Iterable<T> {
    private var max = 0
    private val list = LinkedList<T>()

    init {
        this.max = max
    }

    @Synchronized
    fun clear() {
        list.clear()
    }

    @Synchronized
    fun add(value: T) {
        list.add(value)
        if (list.size > max) {
            list.pop()
        }
    }

    fun setMax(max: Int) {
        this.max = max
    }

    fun size(): Int {
        return list.size
    }

    operator fun get(index: Int): T {
        return list[index]
    }

    override fun iterator(): Iterator<T> {
        return list.iterator()
    }

    @Synchronized
    fun copy(): FixedQueue<T> {
        val q = FixedQueue<T>(max)
        for (v in list) {
            q.add(v)
        }
        return q
    }

    @Synchronized
    fun toList(): ArrayList<T> {
        val ls = ArrayList<T>(max)
        for (v in list) {
            ls.add(v)
        }
        return ls
    }
}
