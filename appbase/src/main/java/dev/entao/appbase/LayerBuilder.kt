@file:Suppress("unused")

package dev.entao.appbase

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.Gravity
import java.util.*

class LayerItemDrawable(var drawable: Drawable) {
    var leftInset = 0
    var topInset = 0
    var rightInset = 0
    var bottomInset = 0


    var gravity: Int = Gravity.NO_GRAVITY
    var width: Int = -1
    var height: Int = -1

    constructor(drawable: Drawable, inset: Int) : this(drawable) {
        bottomInset = inset
        rightInset = inset
        topInset = inset
        leftInset = inset
    }

    fun insetX(n: Int) {
        this.leftInset = n
        this.rightInset = n
    }

    fun insetY(n: Int) {
        this.topInset = n
        this.bottomInset = n
    }

    fun insets(n: Int) {
        this.leftInset = n
        this.rightInset = n
        this.topInset = n
        this.bottomInset = n
    }
}

class LayerBuilder {

    var ls = ArrayList<LayerItemDrawable>()


    fun rect(leftInset: Int, topInset: Int, rightInset: Int, bottomInset: Int, block: ShapeRect.() -> Unit) {
        add(Shapes.rect(block)) {
            this.leftInset = leftInset
            this.rightInset = rightInset
            this.topInset = topInset
            this.bottomInset = bottomInset
        }
    }

    fun line(leftInset: Int, topInset: Int, rightInset: Int, bottomInset: Int, block: ShapeLine.() -> Unit) {
        add(Shapes.line(block)) {
            this.leftInset = leftInset
            this.rightInset = rightInset
            this.topInset = topInset
            this.bottomInset = bottomInset
        }
    }

    fun oval(leftInset: Int, topInset: Int, rightInset: Int, bottomInset: Int, block: ShapeOval.() -> Unit) {
        add(Shapes.oval(block)) {
            this.leftInset = leftInset
            this.rightInset = rightInset
            this.topInset = topInset
            this.bottomInset = bottomInset
        }
    }

    fun ring(leftInset: Int, topInset: Int, rightInset: Int, bottomInset: Int, block: ShapeRing.() -> Unit) {
        add(Shapes.ring(block)) {
            this.leftInset = leftInset
            this.rightInset = rightInset
            this.topInset = topInset
            this.bottomInset = bottomInset
        }
    }

    fun add(d: Drawable, inset: Int) {
        val t = LayerItemDrawable(d, inset)
        ls.add(t)
    }

    fun add(d: Drawable): LayerItemDrawable {
        val t = LayerItemDrawable(d)
        ls.add(t)
        return t
    }

    fun add(d: Drawable, block: LayerItemDrawable.() -> Unit) {
        val t = LayerItemDrawable(d)
        ls.add(t)
        t.block()
    }

    val value: LayerDrawable
        get() {
            val ld = LayerDrawable(ls.map { it.drawable }.toTypedArray())
            for (i in ls.indices) {
                val t = ls[i]
                ld.setLayerInset(i, t.leftInset.dp, t.topInset.dp, t.rightInset.dp, t.bottomInset.dp)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (t.gravity != Gravity.NO_GRAVITY) {
                        ld.setLayerGravity(i, t.gravity)
                    }

                    if (t.width >= 0) {
                        ld.setLayerWidth(i, t.width.dp)
                    }
                    if (t.height >= 0) {
                        ld.setLayerHeight(i, t.height.dp)
                    }
                }
            }
            return ld
        }

    operator fun plusAssign(d: Drawable) {
        this.add(d)
    }
}

fun layerDrawable(block: LayerBuilder.() -> Unit): LayerDrawable {
    val b = LayerBuilder()
    b.block()
    return b.value
}
