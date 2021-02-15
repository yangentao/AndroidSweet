@file:Suppress("unused")

package dev.entao.views

import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import dev.entao.appbase.*
import dev.entao.theme.ColorX
import kotlin.reflect.KClass

/**
 * Created by entaoyang@163.com on 16/3/12.
 */
//同一个页面strID不同即可.
//ViewId是全局的, 当A页面被B页面覆盖时, A和B页面可能都有一个叫nameEdit的名称, 因此, 它们的id必须是相同的.
object ViewID {
    internal val map = HashMap<String, Int>(128)

    internal operator fun set(name: String, nId: Int) {
        map[name] = nId
    }

    operator fun get(name: String): Int {
        var a = map[name]
        if (a == null) {
            a = View.generateViewId()
            map[name] = a
        }
        return a
    }

    operator fun invoke(name: String): Int {
        return get(name)
    }
}

//有可能前面引用后面的ID
fun <T : View> T.named(name: String): T {
    val mapId = ViewID.map[name]
    if (mapId != null) {
        this.id = mapId
        return this
    }
    if (this.id == View.NO_ID) {
        this.id = View.generateViewId()
        ViewID.map[name] = this.id
    } else {
        ViewID.map[name] = this.id
    }
    return this
}


fun <T : View> T.needId(): T {
    if (this.id == View.NO_ID) {
        this.id = View.generateViewId()
    }
    return this
}

fun <T : View> T.requireId(): Int {
    if (this.id == View.NO_ID) {
        this.id = View.generateViewId()
    }
    return this.id
}

fun View.removeFromParent() {
    this.parentGroup?.removeView(this)
}

val View.parentGroup: ViewGroup? get() = this.parent as? ViewGroup

val View.asTextView: TextView
    get() {
        return this as TextView
    }
val View.asButton: Button
    get() {
        return this as Button
    }

fun View.child(n: Int): View? {
    val g = this as? ViewGroup ?: return null
    if (g.childCount > n) {
        return g.getChildAt(n)
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T : View> View.child(cls: KClass<T>): T? {
    val g = this as? ViewGroup ?: return null
    for (c in g.childViews) {
        if (c::class == cls) {
            return c as T
        }
    }
    return null
}


fun <T : View> T.gone(): T {
    visibility = View.GONE
    return this
}

fun <T : View> T.visiable(): T {
    visibility = View.VISIBLE
    return this
}

fun <T : View> T.invisiable(): T {
    visibility = View.INVISIBLE
    return this
}

fun <T : View> T.isGone(): Boolean {
    return visibility == View.GONE
}

fun <T : View> T.isVisiable(): Boolean {
    return visibility == View.VISIBLE
}

fun <T : View> T.isInvisiable(): Boolean {
    return visibility == View.INVISIBLE
}

fun <T : View> T.paddingX(p: Int): T {
    this.setPadding(p.dp, this.paddingTop, p.dp, this.paddingBottom)
    return this
}

fun <T : View> T.paddingX(l: Int, r: Int): T {
    this.setPadding(l.dp, this.paddingTop, r.dp, this.paddingBottom)
    return this
}

fun <T : View> T.paddingY(p: Int): T {
    this.setPadding(this.paddingLeft, p.dp, this.paddingRight, p.dp)
    return this
}

fun <T : View> T.paddingY(t: Int, b: Int): T {
    this.setPadding(this.paddingLeft, t.dp, this.paddingRight, b.dp)
    return this
}

fun <T : View> T.padding(left: Int, top: Int, right: Int, bottom: Int): T {
    this.setPadding(left.dp, top.dp, right.dp, bottom.dp)
    return this
}

fun <T : View> T.paddings(p: Int): T {
    this.setPadding(p.dp, p.dp, p.dp, p.dp)
    return this
}

fun <T : View> T.padding(p: Int): T {
    this.setPadding(p.dp, p.dp, p.dp, p.dp)
    return this
}

fun <T : View> T.paddings(hor: Int, ver: Int): T {
    this.setPadding(hor.dp, ver.dp, hor.dp, ver.dp)
    return this
}

fun <T : View> T.padding(hor: Int, ver: Int): T {
    this.setPadding(hor.dp, ver.dp, hor.dp, ver.dp)
    return this
}

fun <T : View> T.padLeft(n: Int): T {
    this.setPadding(n.dp, this.paddingTop, this.paddingRight, this.paddingBottom)
    return this
}

fun <T : View> T.padTop(n: Int): T {
    this.setPadding(this.paddingLeft, n.dp, this.paddingRight, this.paddingBottom)
    return this
}

fun <T : View> T.padRight(n: Int): T {
    this.setPadding(this.paddingLeft, this.paddingTop, n.dp, this.paddingBottom)
    return this
}

fun <T : View> T.padBottom(n: Int): T {
    this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, n.dp)
    return this
}

fun <T : View> T.backColor(color: Int): T {
    setBackgroundColor(color)
    return this
}

fun <T : View> T.backColor(color: Int, fadeColor: Int): T {
    backColorList(color) {
        lighted(fadeColor)
    }
    return this
}


fun <T : View> T.backColorWhite(): T {
    setBackgroundColor(Color.WHITE)
    return this
}

fun <T : View> T.backColorTrans(): T {
    setBackgroundColor(ColorX.TRANS)
    return this
}

fun <T : View> T.backColorTheme(): T {
    backColor(ColorX.theme)
    return this
}

fun <T : View> T.backColorThemeFade(): T {
    backColor(ColorX.theme, ColorX.fade)
    return this
}

fun <T : View> T.backColorWhiteFade(): T {
    backColor(Color.WHITE, ColorX.fade)
    return this
}

fun <T : View> T.backColorTransFade(): T {
    backColor(ColorX.TRANS, ColorX.fade)
    return this
}

fun <T : View> T.backColorPage(): T {
    setBackgroundColor(ColorX.pageBackground)
    return this
}

fun <T : View> T.backFillFade(fillColor: Int, corner: Int): T {
    val a = ShapeRect(fillColor, corner).value
    val b = ShapeRect(ColorX.fade, corner).value
    backDrawable(a, b)
    return this
}


fun <T : View> T.backRes(@DrawableRes resId: Int): T {
    this.setBackgroundResource(resId)
    return this
}

fun <T : View> T.backDrawable(d: Drawable): T {
    this.background = d
    return this
}


fun <T : View> T.backDrawable(normal: Drawable, pressed: Drawable): T {
    backDrawableList(normal) {
        lighted(pressed)
    }
    return this
}

fun <T : View> T.backDrawable(@DrawableRes resId: Int, @DrawableRes pressed: Int): T {
    backResList(resId) {
        lighted(pressed)
    }
    return this
}

fun <T : View> T.backRect(block: ShapeRect.() -> Unit): T {
    val a = ShapeRect()
    a.block()
    this.background = a.value
    return this
}

fun <T : View> T.backRectRound(corner: Int, block: ShapeRect.() -> Unit): T {
    val a = ShapeRect()
    a.corner(corner)
    a.block()
    this.background = a.value
    this.outlineProvider = OutlineRoundRect(corner)
    return this
}

fun <T : View> T.backRectRoundFade(corner: Int, rippled: Boolean = false, block: ShapeRect.() -> Unit): T {
    this.background = if (rippled) {
        rippleDrawable {
            rippleColors {
                lighted(ColorX.fade)
            }
            this.content = Shapes.rect {
                corner(corner)
                this.block()
            }
            this.mask = Shapes.rect {
                corner(corner)
                fill(ColorX.red)
            }
        }
    } else {
        StateList.drawable {
            normal {
                Shapes.rect {
                    corner(corner)
                    this.block()
                }
            }
            lighted {
                Shapes.rect {
                    corner(corner)
                    fill(ColorX.fade)
                }
            }
        }
    }
    this.outlineProvider = OutlineRoundRect(corner)
    return this
}

//corner 单位dp
class OutlineRoundRect(private val corner: Int) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, view.width, view.height, corner.dpf)
    }
}

fun <T : View> T.outlineRoundRect(corner: Int): T {
    this.outlineProvider = OutlineRoundRect(corner)
    return this
}


fun View.beginAnimation(a: Animation?, onEndCallback: () -> Unit) {
    this.animation?.cancel()
    if (a == null) {
        onEndCallback()
    } else {
        a.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                onEndCallback()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
        this.startAnimation(a)
    }
}

fun View.beginAnimation(a: Animation?) {
    this.animation?.cancel()
    if (a != null) {
        this.startAnimation(a)
    }
}

fun Animation.begin(view: View) {
    this.cancel()
    view.startAnimation(this)
}

fun Animation.begin(view: View, onEndCallback: () -> Unit) {
    this.cancel()
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            onEndCallback()
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }

    })
    view.startAnimation(this)
}
