@file:Suppress("unused")

package dev.entao.views

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import dev.entao.appbase.Bmp
import dev.entao.theme.ColorX
import dev.entao.appbase.dp
import dev.entao.appbase.drawable

/**
 * Created by entaoyang@163.com on 16/3/12.
 */

class Divider(var color: Int = ColorX.divider) {
    //px
    var size: Int = 1
    var begin: Boolean = false
    var mid: Boolean = true
    var end: Boolean = false
    var pad: Int = 0

    fun size(n: Int): Divider {
        this.size = n
        return this
    }

    fun color(color: Int): Divider {
        this.color = color
        return this
    }

    fun begin(b: Boolean = true): Divider {
        this.begin = b
        return this
    }

    fun mid(b: Boolean = true): Divider {
        this.mid = b
        return this
    }

    fun end(b: Boolean = true): Divider {
        this.end = b
        return this
    }

    fun pad(n: Int): Divider {
        this.pad = n
        return this
    }

}

fun <T : LinearLayout> T.divider(): T {
    return this.divider(Divider(ColorX.divider))
}

fun <T : LinearLayout> T.divider(color: Int): T {
    return this.divider(Divider(color))
}

fun <T : LinearLayout> T.divider(block: Divider.() -> Unit): T {
    val ld = Divider()
    ld.block()
    return this.divider(ld)
}

fun <T : LinearLayout> T.divider(ld: Divider): T {
    if (ld.size > 0) {
        val d = Bmp.line(ld.size, ld.size, ld.color).drawable
        this.dividerDrawable = d
        this.dividerPadding = ld.pad.dp
        var n = LinearLayout.SHOW_DIVIDER_NONE
        if (ld.begin) {
            n = n or LinearLayout.SHOW_DIVIDER_BEGINNING
        }
        if (ld.mid) {
            n = n or LinearLayout.SHOW_DIVIDER_MIDDLE
        }
        if (ld.end) {
            n = n or LinearLayout.SHOW_DIVIDER_END
        }
        this.showDividers = n
    }
    return this
}

fun <T : LinearLayout> T.hideDivider(): T {
    this.showDividers = LinearLayout.SHOW_DIVIDER_NONE
    return this
}


fun <T : LinearLayout> T.orientationVertical(): T {
    this.orientation = LinearLayout.VERTICAL
    return this
}

fun <T : LinearLayout> T.orientationHorizontal(): T {
    this.orientation = LinearLayout.HORIZONTAL
    return this
}

fun <T : LinearLayout> T.horizontal(): T {
    this.orientation = LinearLayout.HORIZONTAL
    return this
}

fun <T : LinearLayout> T.vertical(): T {
    this.orientation = LinearLayout.VERTICAL
    return this
}

fun <T : LinearLayout> T.isVertical(): Boolean {
    return this.orientation == LinearLayout.VERTICAL
}

fun <T : LinearLayout> T.gravity(n: Int): T {
    this.gravity = n
    return this
}

fun <T : LinearLayout> T.gravityCenterVertical(): T {
    this.gravity = Gravity.CENTER_VERTICAL
    return this
}

fun <T : LinearLayout> T.gravityCenterHorizontal(): T {
    this.gravity = Gravity.CENTER_HORIZONTAL
    return this
}

@SuppressLint("RtlHardcoded")
fun <T : LinearLayout> T.gravityLeftCenter(): T {
    this.gravity = Gravity.LEFT or Gravity.CENTER
    return this
}

@SuppressLint("RtlHardcoded")
fun <T : LinearLayout> T.gravityRightCenter(): T {
    this.gravity = Gravity.RIGHT or Gravity.CENTER
    return this
}

fun <T : LinearLayout> T.gravityCenter(): T {
    this.gravity = Gravity.CENTER
    return this
}


fun LinearLayout.grayLine(size: Int) {
    this.grayLine(size, ColorX.divider) {}
}

fun LinearLayout.grayLine(size: Int, color: Int = ColorX.divider, block: LinearParams.() -> Unit) {
    view {
        this.backColor(color)
        val ver = isVertical()
        linearParams {
            if (ver) {
                widthFill.height(size).marginY(1)
            } else {
                width(size).heightFill.marginX(1)
            }
            this.block()
        }
    }
}


fun LinearLayout.addFlex(weight: Double = 1.0): View {
    return view {
        linearParams {
            weight(weight)
            if (this@addFlex.isVertical()) {
                widthFill.height(0)
            } else {
                heightFill.width(0)
            }
        }
        invisiable()
    }
}
