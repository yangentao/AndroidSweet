@file:Suppress("unused")

package dev.entao.base

import java.text.Collator
import java.util.*

const val UTF8 = "UTF-8"
const val PROGRESS_DELAY = 50

typealias BlockUnit = () -> Unit

fun errorArg(message: Any): Nothing = throw IllegalArgumentException(message.toString())


fun printX(vararg vs: Any?) {
    val s = vs.joinToString(" ") {
        it?.toString() ?: "null"
    }
    println(s)
}






val collatorChina: Collator by lazy { Collator.getInstance(Locale.CHINA) }
val chinaComparator = Comparator<String> { left, right -> collatorChina.compare(left, right) }

class ChinaComparator<T>(val block: (T) -> String) : Comparator<T> {
    override fun compare(o1: T, o2: T): Int {
        return chinaComparator.compare(block(o1), block(o2))
    }
}




fun Sleep(millSeconds: Long) {
    try {
        Thread.sleep(millSeconds)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }

}

fun Sleep(ms: Int) {
    Sleep(ms.toLong())
}


object Rand {
    val random = Random(System.nanoTime())

    //[0, bound)
    fun int(bound: Int): Int {
        return random.nextInt(bound)
    }

    //[from, toValue)
    fun int(from: Int, toValue: Int): Int {
        val dist = toValue - from
        if (dist > 0) {
            return random.nextInt(dist) + dist
        }
        error("Assertion failed")
    }

    //[1000, 9999]
    fun code4(): String {
        return this.int(1000, 10000).toString()
    }

    //[100000,999999]
    fun code6(): String {
        return this.int(100000, 1000000).toString()
    }
}