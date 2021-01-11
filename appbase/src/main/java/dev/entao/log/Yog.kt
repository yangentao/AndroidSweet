package dev.entao.log

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by entaoyang@163.com on 2018/11/8.
 */

object Yog {

    private var _printer: YogPrinter? = LogcatPrinter
    var level = LogLevel.DEBUG

    fun init(context: Context) {
        val d = context.externalCacheDir
        val ld = File(d, "xlog")
        if (!ld.exists()) {
            ld.mkdirs()
            ld.mkdir()
        }
        setPrinter(
            YogTree(
                LogcatPrinter,
                YogDir(ld)
            )
        )
    }

    fun setPrinter(p: YogPrinter) {
        _printer?.uninstall()
        _printer = p
    }

    fun flush() {
        _printer?.flush()
    }

    fun d(vararg args: Any?) {
        printMessage(LogLevel.DEBUG, *args)
    }

    fun w(vararg args: Any?) {
        printMessage(LogLevel.WARN, *args)
    }

    fun e(vararg args: Any?) {
        printMessage(LogLevel.ERROR, *args)
        _printer?.flush()
    }

    fun i(vararg args: Any?) {
        printMessage(LogLevel.INFO, *args)
    }


    fun fatal(vararg args: Any?) {
        e(*args)
        throw RuntimeException("fatal error!")
    }

    fun formatMsg(level: LogLevel, msg: String): String {
        val sb = StringBuilder(msg.length + 64)
        val ff = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val date = ff.format(Date(System.currentTimeMillis()))
        sb.append(date)
        sb.append(String.format(Locale.getDefault(), "%6d ", Thread.currentThread().id))
        sb.append(level.name)
        sb.append(" ")
        sb.append(msg)
        return sb.toString()
    }

    fun printMessage(level: LogLevel, vararg args: Any?) {
        if (Yog.level != LogLevel.DISABLE && level.ge(Yog.level)) {
            val s: String = args.joinToString(" ") {
                toLogString(it)
            }
            _printer?.printLine(level, s)
        }
    }

    fun toLogString(obj: Any?): String {
        if (obj == null) {
            return "null"
        }
        if (obj is String) {
            return obj
        }
        if (obj.javaClass.isPrimitive) {
            return obj.toString()
        }

        if (obj is Throwable) {
            val sw = StringWriter(512)
            val pw = PrintWriter(sw)
            obj.printStackTrace(pw)
            return sw.toString()
        }

        if (obj is Array<*>) {
            val s = obj.joinToString(",") { toLogString(it) }
            return "ARRAY[$s]"
        }
        if (obj is List<*>) {
            val s = obj.joinToString(", ") { toLogString(it) }
            return "LIST[$s]"
        }
        if (obj is Map<*, *>) {
            val s = obj.map { "${toLogString(it.key)} = ${toLogString(it.value)}" }.joinToString(",")
            return "MAP{$s}"
        }
        if (obj is Iterable<*>) {
            val s = obj.joinToString(", ") { toLogString(it) }
            return "ITERABLE[$s]"
        }
        return obj.toString()
    }
}