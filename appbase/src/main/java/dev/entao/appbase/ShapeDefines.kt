@file:Suppress("unused")

package dev.entao.appbase

import android.graphics.drawable.GradientDrawable


open class ShapeBase(shape: Int) {
    val value: GradientDrawable = GradientDrawable()

    init {
        value.shape = shape
    }
}

inline fun <reified T : ShapeBase> T.fill(color: Int): T {
    value.setColor(color)
    return this
}

inline fun <reified T : ShapeBase> T.stroke(width: Int, color: Int): T {
    value.setStroke(width.dp, color)
    return this
}

inline fun <reified T : ShapeBase> T.strokeDash(width: Int, color: Int, dashWidth: Int, dashGap: Int): T {
    value.setStroke(width.dp, color, dashWidth.dpf, dashGap.dpf)
    return this
}

inline fun <reified T : ShapeBase> T.alpha(n: Int): T {
    value.alpha = n
    return this
}

inline fun <reified T : ShapeBase> T.size(w: Int, h: Int = w): T {
    value.setSize(w.dp, h.dp)
    return this
}

inline fun <reified T : ShapeBase> T.topToBottom(): T {
    value.orientation = GradientDrawable.Orientation.TOP_BOTTOM
    return this
}

inline fun <reified T : ShapeBase> T.leftToRight(): T {
    value.orientation = GradientDrawable.Orientation.LEFT_RIGHT
    return this
}

class ShapeRect() : ShapeBase(GradientDrawable.RECTANGLE) {


    constructor(fillColor: Int) : this() {
        this.fill(fillColor)
    }

    constructor(fillColor: Int, corner: Int) : this() {
        this.fill(fillColor)
        this.corner(corner)
    }


    fun corner(corner: Int): ShapeRect {
        value.cornerRadius = corner.dpf
        return this
    }

    fun corners(topLeft: Int, topRight: Int, bottomRight: Int, bottomLeft: Int): ShapeRect {
        val f1 = topLeft.dpf
        val f2 = topRight.dpf
        val f3 = bottomRight.dpf
        val f4 = bottomLeft.dpf
        value.cornerRadii = floatArrayOf(f1, f1, f2, f2, f3, f3, f4, f4)
        return this
    }


}


class ShapeLine : ShapeBase(GradientDrawable.LINE)

class ShapeOval() : ShapeBase(GradientDrawable.OVAL) {
    constructor(fillColor: Int) : this() {
        fill(fillColor)
    }
}

class ShapeRing : ShapeBase(GradientDrawable.RING)

object Shapes {

    fun rect(block: ShapeRect.() -> Unit): GradientDrawable {
        val r = ShapeRect()
        r.block()
        return r.value
    }

    fun oval(block: ShapeOval.() -> Unit): GradientDrawable {
        val a = ShapeOval()
        a.block()
        return a.value
    }

    //画虚线需要关闭硬件加速: view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    fun line(block: ShapeLine.() -> Unit): GradientDrawable {
        val a = ShapeLine()
        a.block()
        return a.value
    }

    fun ring(block: ShapeRing.() -> Unit): GradientDrawable {
        val a = ShapeRing()
        a.block()
        return a.value
    }
}
