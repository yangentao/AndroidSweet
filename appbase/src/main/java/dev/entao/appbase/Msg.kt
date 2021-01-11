@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.appbase

import androidx.annotation.Keep
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

/**
 * Created by yangentao on 16/3/12.
 */

@Keep
class Msg(val msg: String) {
    //当前线程同步调用
    var sync: Boolean = false

    //异步: 主线程调用
    var mainThread: Boolean = true

    //异步: 合并delay毫秒内的消息
    var mergeDelay: Long = 0L

    //消息广播完成回调
    var onFireEnd: ((Msg) -> Unit)? = null

    var result = ArrayList<Any>()
    var n1: Long = 0
    var n2: Long = 0
    var s1: String = ""
    var s2: String = ""
    var b1: Boolean = false
    var b2: Boolean = false
    var cls: KClass<*>? = null
    var any: Any? = null

    constructor(cls: KClass<*>) : this(cls.qualifiedName!!)

    override fun hashCode(): Int {
        return msg.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Msg) {
            return msg == other.msg &&
                    n1 == other.n1 &&
                    n2 == other.n2 &&
                    s1 == other.s1 &&
                    s2 == other.s2 &&
                    b1 == other.b1 &&
                    b2 == other.b2

        }
        return false
    }


    fun isMsg(vararg msgs: String): Boolean {
        return this.msg in msgs
    }


    fun isMsg(vararg classes: KClass<*>): Boolean {
        return classes.find { this.msg == it.qualifiedName } != null
    }

    operator fun contains(msg: String): Boolean {
        return isMsg(msg)
    }


    operator fun contains(msg: KClass<*>): Boolean {
        return isMsg(msg)
    }

    fun argB(b1: Boolean, b2: Boolean = false): Msg {
        this.b1 = b1
        this.b2 = b2
        return this
    }

    fun argN(n1: Long, n2: Long = 0): Msg {
        this.n1 = n1
        this.n2 = n2
        return this
    }

    fun argS(s1: String, s2: String = ""): Msg {
        this.s1 = s1
        this.s2 = s2
        return this
    }

    fun addResult(value: Any): Msg {
        this.result.add(value)
        return this
    }

    fun ret(value: Any): Msg {
        this.result.add(value)
        return this
    }
}


fun interface MsgListener {
    fun onMsg(msg: Msg)
}

@Keep
object MsgCenter {
    private val allList = ArrayList<WeakReference<MsgListener>>()

    @Synchronized
    fun listenAll(listener: MsgListener) {
        for (wl in allList) {
            if (wl.get() == listener) {
                return
            }
        }
        allList.add(WeakReference(listener))
    }


    @Synchronized
    fun remove(listener: MsgListener) {
        allList.removeAll { it.get() == null || it.get() == listener }
    }


    fun fireCurrent(msg: Msg) {
        val ls2 = ArrayList<MsgListener>()
        sync(this) {
            val ls = allList.filter { it.get() != null }.map { it.get() }
            allList.retainAll { it.get() != null }
            ls.filterNotNullTo(ls2)
        }

        ls2.forEach {
            it.onMsg(msg)
        }
        msg.onFireEnd?.invoke(msg)
        msg.onFireEnd = null
    }

    fun fire(msg: Msg) {
        if (msg.sync) {
            fireCurrent(msg)
            return
        }
        if (msg.mergeDelay > 0L) {
            mergeAction("MsgCenter.mergeAction" + msg.msg, msg.mergeDelay) {
                if (msg.mainThread) {
                    Task.fore {
                        fireCurrent(msg)
                    }
                } else {
                    Task.back {
                        fireCurrent(msg)
                    }
                }
            }
            return
        }
        if (msg.mainThread) {
            Task.fore {
                fireCurrent(msg)
            }
        } else {
            Task.back {
                fireCurrent(msg)
            }
        }
    }

}

fun String.fire() {
    val m = Msg(this)
    MsgCenter.fire(m)
}

fun KClass<*>.fire() {
    val m = Msg(this)
    MsgCenter.fire(m)
}

fun String.fire(block: Msg.() -> Unit) {
    val m = Msg(this)
    m.block()
    MsgCenter.fire(m)
}

fun KClass<*>.fire(block: Msg.() -> Unit) {
    val m = Msg(this)
    m.block()
    MsgCenter.fire(m)
}