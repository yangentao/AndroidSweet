@file:Suppress("unused")

package dev.entao.views

import android.view.ViewGroup
import dev.entao.appbase.dp

val <T : GroupParams> T.wrap: T
    get() {
        this.height = GroupParams.WRAP_CONTENT
        this.width = GroupParams.WRAP_CONTENT
        return this
    }

val <T : GroupParams> T.widthWrap: T
    get() {
        this.width = GroupParams.WRAP_CONTENT
        return this
    }


val <T : GroupParams> T.heightWrap: T
    get() {
        this.height = GroupParams.WRAP_CONTENT
        return this
    }

val <T : GroupParams> T.fill: T
    get() {
        this.width = GroupParams.MATCH_PARENT
        this.height = GroupParams.MATCH_PARENT
        return this
    }

val <T : GroupParams> T.widthFill: T
    get() {
        this.width = GroupParams.MATCH_PARENT
        return this
    }

val <T : GroupParams> T.heightFill: T
    get() {
        this.height = GroupParams.MATCH_PARENT
        return this
    }

fun <T : GroupParams> T.size(w: Int, h: Int = w): T {
    return width(w).height(h)
}


fun <T : GroupParams> T.width(w: Int): T {
    if (w > 0) {
        this.width = w.dp
    } else {
        this.width = w
    }
    return this
}

fun <T : GroupParams> T.widthPx(w: Int): T {
    this.width = w
    return this
}


fun <T : GroupParams> T.height(h: Int): T {
    if (h > 0) {
        this.height = h.dp
    } else {
        this.height = 0
    }
    return this
}

fun <T : GroupParams> T.heightPx(h: Int): T {
    this.height = h
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.marginLeft(v: Int): T {
    this.leftMargin = v.dp
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.marginRight(v: Int): T {
    this.rightMargin = v.dp
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.marginTop(v: Int): T {
    this.topMargin = v.dp
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.marginBottom(v: Int): T {
    this.bottomMargin = v.dp
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.margins(m: Int): T {
    val v = m.dp
    this.setMargins(v, v, v, v)
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.margins(left: Int, top: Int, right: Int, bottom: Int): T {
    this.setMargins(left.dp, top.dp, right.dp, bottom.dp)
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.marginsPx(left: Int, top: Int, right: Int, bottom: Int): T {
    this.setMargins(left, top, right, bottom)
    return this
}

fun <T : ViewGroup.MarginLayoutParams> T.margins(hor: Int, ver: Int): T {
    return margins(hor, ver, hor, ver)
}

fun <T : ViewGroup.MarginLayoutParams> T.marginX(left: Int, right: Int = left): T {
    this.setMargins(left.dp, this.topMargin, right.dp, this.bottomMargin)
    return this
}


fun <T : ViewGroup.MarginLayoutParams> T.marginY(top: Int, bottom: Int = top): T {
    this.setMargins(this.leftMargin, top.dp, this.rightMargin, bottom.dp)
    return this
}


