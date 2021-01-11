package dev.entao.log

import android.util.Log
import dev.entao.base.substr

/**
 * Created by entaoyang@163.com on 2018/11/8.
 */
enum class LogLevel(val n: Int) {
    DISABLE(0), DEBUG(3), INFO(4), WARN(5), ERROR(6), FATAIL(7);

    //>=
    fun ge(level: LogLevel): Boolean {
        return this.ordinal >= level.ordinal
    }
}

interface YogPrinter {
    fun flush()
    fun printLine(level: LogLevel, msg: String)
    fun uninstall() {}
}

class YogTree(vararg ps: YogPrinter) : YogPrinter {
    val all = ArrayList<YogPrinter>()

    init {
        all += ps
    }

    override fun flush() {
        all.forEach { it.flush() }
    }

    override fun printLine(level: LogLevel, msg: String) {
        all.forEach { it.printLine(level, msg) }
    }

    override fun uninstall() {
        for (p in all) {
            p.uninstall()
        }
    }

}

object YogConsole : YogPrinter {

    override fun printLine(level: LogLevel, msg: String) {
        var s = Yog.formatMsg(level, msg)
        while (s.length > 1024) {
            println(s.substring(0, 1024))
            s = s.substring(1024)
        }
        println(s)
    }

    override fun flush() {

    }

}

object LogcatPrinter : YogPrinter {
    var tagName = "ylog"

    override fun printLine(level: LogLevel, msg: String) {
        var n = level.n
        if (n < Log.VERBOSE) {
            n = Log.VERBOSE
        }
        var from = 0
        while (from + 1000 < msg.length) {
            Log.println(n, tagName, msg.substr(from, 1000))
            from += 1000
        }
        if (from < msg.length) {
            Log.println(n, tagName, msg.substring(from))
        }
    }

    override fun flush() {

    }
}