@file:Suppress("unused")

package dev.entao.views

import android.view.View
import android.view.ViewGroup
import kotlin.reflect.KClass

/**
 * Created by entaoyang@163.com on 2018-04-16.
 */

val ViewGroup.childViews: List<View>
    get() {
        val ls = ArrayList<View>()
        for (i in 0 until this.childCount) {
            ls += this.getChildAt(i)
        }
        return ls
    }

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.last(cls: KClass<T>): T? {
    return this.last {
        it::class == cls
    } as? T
}

fun ViewGroup.last(block: (View) -> Boolean): View? {
    val ls = this.childViews.reversed()
    for (v in ls) {
        if (block(v)) {
            return v
        }
    }
    for (v in ls) {
        if (v is ViewGroup) {
            val c = v.last(block)
            if (c != null) {
                return c
            }
        }
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.first(cls: KClass<T>): T? {
    return this.first {
        it::class == cls
    } as? T
}

fun ViewGroup.first(block: (View) -> Boolean): View? {
    for (v in this.childViews) {
        if (block(v)) {
            return v
        }
    }
    for (v in this.childViews) {
        if (v is ViewGroup) {
            val c = v.first(block)
            if (c != null) {
                return c
            }
        }
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.findChild(cls: KClass<T>): T? {
    for (i in 0 until this.childCount) {
        val c = getChildAt(i)
        if (c::class == cls) {
            return c as T
        }
    }
    return null
}


@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.childrenOf(cls: KClass<T>): List<T> {
    return childViews.filter { it::class == cls }.map { it as T }
}