package dev.entao.base

import dev.entao.log.Yog


/**
 * Created by yangentao on 2015/11/21.
 * entaoyang@163.com
 */
class Tick {
    private var start_time: Long = System.currentTimeMillis()
    private var end_time: Long = 0

    fun restart() {
        start_time = System.currentTimeMillis()
    }

    fun end(prefix: String = ""): Long {
        return end(0, prefix)
    }

    fun end(warnLevel: Long, prefix: String): Long {
        end_time = System.currentTimeMillis()
        val tick = end_time - start_time
        if (warnLevel in 1 until tick) {
            Yog.w("[TimeTick]", prefix, ":", tick, "(expect<", warnLevel, ")")
        } else {
            Yog.d("[TimeTick]", prefix, ":", tick)
        }
        start_time = System.currentTimeMillis()
        return tick
    }
}

inline fun <R> tick(msg: String = "", block: () -> R): R {
    val t = Tick()
    val r = block()
    t.end(msg)
    return r
}

