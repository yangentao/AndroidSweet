@file:Suppress("unused")

package dev.entao.views

import android.view.View
import android.widget.RelativeLayout

/**
 * Created by entaoyang@163.com on 2016-07-21.
 */
val RParam: RelativeLayout.LayoutParams
    get() {
        return RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
    }

val <T : RelativeLayout.LayoutParams> T.parentHor: T
    get() {
        addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        return this
    }
val <T : RelativeLayout.LayoutParams> T.parentVer: T
    get() {
        addRule(RelativeLayout.ALIGN_PARENT_TOP)
        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        return this
    }
val <T : RelativeLayout.LayoutParams> T.parentFill: T
    get() {
        addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        addRule(RelativeLayout.ALIGN_PARENT_TOP)
        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        return this
    }
val <T : RelativeLayout.LayoutParams> T.parentBottom: T
    get() {
        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        return this
    }


val <T : RelativeLayout.LayoutParams> T.parentLeft: T
    get() {
        addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        return this
    }


val <T : RelativeLayout.LayoutParams> T.parentRight: T
    get() {
        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        return this
    }


val <T : RelativeLayout.LayoutParams> T.parentTop: T
    get() {
        addRule(RelativeLayout.ALIGN_PARENT_TOP)
        return this
    }


val <T : RelativeLayout.LayoutParams> T.parentCenter: T
    get() {
        addRule(RelativeLayout.CENTER_IN_PARENT)
        return this
    }


val <T : RelativeLayout.LayoutParams> T.parentCenterX: T
    get() {
        addRule(RelativeLayout.CENTER_HORIZONTAL)
        return this
    }


val <T : RelativeLayout.LayoutParams> T.parentCenterY: T
    get() {
        addRule(RelativeLayout.CENTER_VERTICAL)
        return this
    }

fun <T : RelativeLayout.LayoutParams> T.centerX(anchor: Int): T {
    addRule(RelativeLayout.CENTER_HORIZONTAL, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.centerX(v: View): T {
    return centerX(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.centerY(anchor: Int): T {
    addRule(RelativeLayout.CENTER_VERTICAL, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.centerY(v: View): T {
    return centerY(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.above(anchor: Int): T {
    addRule(RelativeLayout.ABOVE, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.above(v: View): T {
    return this.above(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.baseline(anchor: Int): T {
    addRule(RelativeLayout.ALIGN_BASELINE, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.baseline(v: View): T {
    return this.baseline(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.bottom(anchor: Int): T {
    addRule(RelativeLayout.ALIGN_BOTTOM, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.bottom(v: View): T {
    return this.bottom(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.left(anchor: Int): T {
    addRule(RelativeLayout.ALIGN_LEFT, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.left(v: View): T {
    return this.left(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.right(anchor: Int): T {
    addRule(RelativeLayout.ALIGN_RIGHT, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.right(v: View): T {
    return this.right(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.top(anchor: Int): T {
    addRule(RelativeLayout.ALIGN_TOP, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.top(v: View): T {
    return this.top(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.below(anchor: Int): T {
    addRule(RelativeLayout.BELOW, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.below(v: View): T {
    return this.below(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.toLeft(anchor: Int): T {
    addRule(RelativeLayout.LEFT_OF, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.toLeft(v: View): T {
    return this.toLeft(v.requireId())
}

fun <T : RelativeLayout.LayoutParams> T.toRight(anchor: Int): T {
    addRule(RelativeLayout.RIGHT_OF, anchor)
    return this
}

fun <T : RelativeLayout.LayoutParams> T.toRight(v: View): T {
    return this.toRight(v.requireId())
}