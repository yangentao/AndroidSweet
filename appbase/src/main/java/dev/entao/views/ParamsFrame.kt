@file:Suppress("unused")

package dev.entao.views

import android.view.Gravity
import android.widget.FrameLayout

/**
 * Created by entaoyang@163.com on 2016-10-29.
 */


val <T : FrameLayout.LayoutParams> T.gravityTop: T
    get() {
        gravity = Gravity.TOP
        return this
    }


val <T : FrameLayout.LayoutParams> T.gravityTopCenter: T
    get() {
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityTopLeft: T
    get() {
        gravity = Gravity.TOP or Gravity.START
        return this
    }
val <T : FrameLayout.LayoutParams> T.gravityTopRight: T
    get() {
        gravity = Gravity.TOP or Gravity.END
        return this
    }
val <T : FrameLayout.LayoutParams> T.gravityBottom: T
    get() {
        gravity = Gravity.BOTTOM
        return this
    }


val <T : FrameLayout.LayoutParams> T.gravityBottomCenter: T
    get() {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        return this
    }


val <T : FrameLayout.LayoutParams> T.gravityBottomLeft: T
    get() {
        gravity = Gravity.BOTTOM or Gravity.START
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityBottomRight: T
    get() {
        gravity = Gravity.BOTTOM or Gravity.END
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityLeft: T
    get() {
        gravity = Gravity.START
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityLeftCenter: T
    get() {
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityRight: T
    get() {
        gravity = Gravity.END
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityRightCenter: T
    get() {
        gravity = Gravity.END or Gravity.CENTER_VERTICAL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityFill: T
    get() {
        gravity = Gravity.FILL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityFillY: T
    get() {
        gravity = Gravity.FILL_VERTICAL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityFillX: T
    get() {
        gravity = Gravity.FILL_HORIZONTAL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityCenterY: T
    get() {
        gravity = Gravity.CENTER_VERTICAL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityCenterX: T
    get() {
        gravity = Gravity.CENTER_HORIZONTAL
        return this
    }

val <T : FrameLayout.LayoutParams> T.gravityCenter: T
    get() {
        gravity = Gravity.CENTER
        return this
    }


fun <T : FrameLayout.LayoutParams> T.gravity(g: Int): T {
    gravity = g
    return this
}
