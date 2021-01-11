@file:Suppress("unused")

package dev.entao.views

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.LinearLayout

/**
 * Created by entaoyang@163.com on 2016-07-21.
 */


val <T : LinearLayout.LayoutParams> T.flexX: T
    get() {
        return this.width(0).weight(1)
    }


val <T : LinearLayout.LayoutParams> T.flexY: T
    get() {
        return this.height(0).weight(1)
    }

fun <T : LinearLayout.LayoutParams> T.widthFlex(w: Int): T {
    weight = w.toFloat()
    width = 0
    return this
}

fun <T : LinearLayout.LayoutParams> T.heightFlex(w: Int): T {
    weight = w.toFloat()
    height = 0
    return this
}

fun <T : LinearLayout.LayoutParams> T.weight(w: Number): T {
    weight = w.toFloat()
    return this
}

val <T : LinearLayout.LayoutParams> T.gravityTop: T
    get() {
        gravity = Gravity.TOP
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityTopCenter: T
    get() {
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        return this
    }

val <T : LinearLayout.LayoutParams> T.gravityBottom: T
    get() {
        gravity = Gravity.BOTTOM
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityBottomCenter: T
    get() {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityLeft: T
    @SuppressLint("RtlHardcoded")
    get() {
        gravity = Gravity.LEFT
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityLeftCenter: T
    @SuppressLint("RtlHardcoded")
    get() {
        gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        return this
    }

val <T : LinearLayout.LayoutParams> T.gravityRight: T
    @SuppressLint("RtlHardcoded")
    get() {
        gravity = Gravity.RIGHT
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityRightCenter: T
    @SuppressLint("RtlHardcoded")
    get() {
        gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityFill: T
    get() {
        gravity = Gravity.FILL
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityFillY: T
    get() {
        gravity = Gravity.FILL_VERTICAL
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityFillX: T
    get() {
        gravity = Gravity.FILL_HORIZONTAL
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityCenterY: T
    get() {
        gravity = Gravity.CENTER_VERTICAL
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityCenterX: T
    get() {
        gravity = Gravity.CENTER_HORIZONTAL
        return this
    }


val <T : LinearLayout.LayoutParams> T.gravityCenter: T
    get() {
        gravity = Gravity.CENTER
        return this
    }

