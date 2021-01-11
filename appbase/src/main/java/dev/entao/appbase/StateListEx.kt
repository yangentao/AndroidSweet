@file:Suppress("unused")

package dev.entao.appbase

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources


enum class ViewState(val value: Int) {
    Selected(android.R.attr.state_selected), Unselected(-android.R.attr.state_selected),
    Pressed(android.R.attr.state_pressed), Unpressed(-android.R.attr.state_pressed),
    Enabled(android.R.attr.state_enabled), Disabled(-android.R.attr.state_enabled),
    Checked(android.R.attr.state_checked), Unchecked(-android.R.attr.state_checked),
    Focused(android.R.attr.state_focused), Unfocused(-android.R.attr.state_focused),
    Normal(0)
}


class ViewStateBuilder<T>() {
    private val items: ArrayList<Pair<Int, T>> = ArrayList()
    private var normalValue: T? = null

    val values: ArrayList<Pair<Int, T>>
        get() {
            if (normalValue != null) {
                items += ViewState.Normal.value to normalValue!!
            }
            return items
        }

    constructor(normalValue: T) : this() {
        this.normalValue = normalValue
    }

    fun normal(v: T): ViewStateBuilder<T> {
        this.normalValue = v
        return this
    }

    fun normal(block: () -> T) {
        this.normalValue = block()
    }

    fun lighted(v: T): ViewStateBuilder<T> {
        pressed(v)
        selected(v)
        focused(v)
        return this
    }

    fun lighted(block: () -> T) {
        lighted(block())
    }

    fun selected(v: T): ViewStateBuilder<T> {
        items += ViewState.Selected.value to v
        return this
    }

    fun selected(block: () -> T) {
        selected(block())
    }

    fun unselected(v: T): ViewStateBuilder<T> {
        items += ViewState.Unselected.value to v
        return this
    }

    fun unselected(block: () -> T) {
        unselected(block())
    }

    fun pressed(v: T): ViewStateBuilder<T> {
        items += ViewState.Pressed.value to v
        return this
    }

    fun pressed(block: () -> T) {
        pressed(block())
    }

    fun unpressed(v: T): ViewStateBuilder<T> {
        items += ViewState.Unpressed.value to v
        return this
    }

    fun unpressed(block: () -> T) {
        unpressed(block())
    }

    fun enabled(v: T): ViewStateBuilder<T> {
        items += ViewState.Enabled.value to v
        return this
    }

    fun enabled(block: () -> T) {
        enabled(block())
    }

    fun disabled(v: T): ViewStateBuilder<T> {
        items += ViewState.Disabled.value to v
        return this
    }

    fun disabled(block: () -> T) {
        disabled(block())
    }

    fun checked(v: T): ViewStateBuilder<T> {
        items += ViewState.Checked.value to v
        return this
    }

    fun checked(block: () -> T) {
        checked(block())
    }

    fun unchecked(v: T): ViewStateBuilder<T> {
        items += ViewState.Unchecked.value to v
        return this
    }

    fun unchecked(block: () -> T) {
        unchecked(block())
    }

    fun focused(v: T): ViewStateBuilder<T> {
        items += ViewState.Focused.value to v
        return this
    }

    fun focused(block: () -> T) {
        focused(block())
    }

    fun unfocused(v: T): ViewStateBuilder<T> {
        items += ViewState.Unfocused.value to v
        return this
    }

    fun unfocused(block: () -> T) {
        unfocused(block())
    }
}

val ViewStateBuilder<Int>.colorList: ColorStateList
    get() {
        val a: Array<IntArray> = values.map { intArrayOf(it.first) }.toTypedArray()
        val b: IntArray = values.map { it.second }.toIntArray()
        return ColorStateList(a, b)
    }

val ViewStateBuilder<Int>.colorDrawables: StateListDrawable
    get() {
        val ld = StateListDrawable()
        for (p in values) {
            ld.addState(intArrayOf(p.first), ColorDrawable(p.second))
        }
        return ld
    }

val ViewStateBuilder<Int>.resDrawables: StateListDrawable
    get() {
        val ld = StateListDrawable()
        for (p in values) {
            val d = AppCompatResources.getDrawable(App.inst, p.second)!!
            ld.addState(intArrayOf(p.first), d)
        }
        return ld
    }

val ViewStateBuilder<Drawable>.drawableList: StateListDrawable
    get() {
        val ld = StateListDrawable()
        for (p in values) {
            ld.addState(intArrayOf(p.first), p.second)
        }
        return ld
    }

object StateList {
    fun color(color: Int, block: ViewStateBuilder<Int>.() -> Unit): ColorStateList {
        val m = ViewStateBuilder(color)
        m.block()
        return m.colorList
    }

    fun color(block: ViewStateBuilder<Int>.() -> Unit): ColorStateList {
        val m = ViewStateBuilder<Int>()
        m.block()
        return m.colorList
    }

    fun colorDrawable(color: Int, block: ViewStateBuilder<Int>.() -> Unit): StateListDrawable {
        val m = ViewStateBuilder(color)
        m.block()
        return m.colorDrawables
    }

    fun drawable(normal: Drawable, block: ViewStateBuilder<Drawable>.() -> Unit): StateListDrawable {
        val m = ViewStateBuilder(normal)
        m.block()
        return m.drawableList
    }

    fun drawable(block: ViewStateBuilder<Drawable>.() -> Unit): StateListDrawable {
        val m = ViewStateBuilder<Drawable>()
        m.block()
        return m.drawableList
    }

    fun resDrawable(normalRes: Int, block: ViewStateBuilder<Int>.() -> Unit): StateListDrawable {
        val m = ViewStateBuilder(normalRes)
        m.block()
        return m.resDrawables
    }

    fun lightColors(normalValue: Int, lightValue: Int): ColorStateList {
        return color(normalValue) {
            lighted(lightValue)
        }
    }

    fun lightColorDrawables(normalValue: Int, lightValue: Int): StateListDrawable {
        return colorDrawable(normalValue) {
            lighted(lightValue)
        }
    }

    fun lightResDrawables(normalValue: Int, lightValue: Int): StateListDrawable {
        return resDrawable(normalValue) {
            lighted(lightValue)
        }
    }

    fun lightDrawables(normalValue: Drawable, lightValue: Drawable): StateListDrawable {
        return drawable(normalValue) {
            lighted(lightValue)
        }
    }
}


fun <T : TextView> T.textColorList(color: Int, block: ViewStateBuilder<Int>.() -> Unit): T {
    val a = StateList.color(color, block)
    this.setTextColor(a)
    return this
}

fun <T : View> T.backColorList(color: Int, block: ViewStateBuilder<Int>.() -> Unit): T {
    this.background = StateList.colorDrawable(color, block)
    return this
}

fun <T : View> T.backResList(@DrawableRes normalRes: Int, block: ViewStateBuilder<Int>.() -> Unit): T {
    this.background = StateList.resDrawable(normalRes, block)
    return this
}

fun <T : View> T.backDrawableList(normal: Drawable, block: ViewStateBuilder<Drawable>.() -> Unit): T {
    this.background = StateList.drawable(normal, block)
    return this
}