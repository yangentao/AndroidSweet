package dev.entao.views

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.widget.*
import androidx.core.widget.CompoundButtonCompat
import dev.entao.appbase.*
import dev.entao.theme.ColorX
import dev.entao.theme.Dimen


fun <T : EditText> T.editStyleDefault() {
    padding(12, 2, 12, 2)
}

fun <T : EditText> T.editStyleLine() {
    editStyleDefault()

    this.background = StateList.drawable {
        normal {
            layerDrawable {
                rect(-2, -2, -2, 0) {
                    fill(Color.TRANSPARENT)
                    stroke(1, ColorX.editBorder)
                }
            }
        }
        focused {
            layerDrawable {
                rect(-2, -2, -2, 0) {
                    fill(Color.TRANSPARENT)
                    stroke(2, ColorX.editBorderFocus)
                }
            }
        }
    }
    outlineRoundRect(0)
}

fun <T : EditText> T.editStyleCorner(color: Int = ColorX.editBorder, focusColor: Int = ColorX.editBorderFocus, corner: Int = 3) {
    editStyleDefault()
    this.background = StateList.drawable {
        normal {
            ShapeRect(Color.TRANSPARENT, corner).stroke(1, color).value
        }
        focused {
            ShapeRect(Color.TRANSPARENT, corner).stroke(1, focusColor).value
        }
    }
    outlineRoundRect(corner)
}

//根据layoutParams获取高度, 用来决定圆角半径
fun <T : EditText> T.editStyleRound(color: Int = ColorX.editBorder, focusColor: Int = ColorX.editBorderFocus, strokeWidth: Int = 1) {
    editStyleDefault()
    val r = px2dp(this.layoutParams.height / 2)
    this.background = StateList.drawable {
        normal {
            ShapeRect(Color.TRANSPARENT, r).stroke(strokeWidth, color).value
        }
        focused {
            ShapeRect(Color.TRANSPARENT, r).stroke(strokeWidth, focusColor).value
        }
    }
    padding(15, 2, 15, 2)
    outlineRoundRect(r)
}

fun <T : TextView> T.textColorWhite(): T {
    this.setTextColor(Color.WHITE)
    return this
}

fun <T : TextView> T.textWhite(): T {
    this.setTextColor(Color.WHITE)
    return this
}

//--size--
fun <T : TextView> T.textSizeTitle(): T {
    return textSize(Dimen.textTitle)
}

fun <T : TextView> T.textSizePrimary(): T {
    return textSize(Dimen.textPrimary)
}

fun <T : TextView> T.textSizeSecondary(): T {
    return textSize(Dimen.textSecondary)
}

fun <T : TextView> T.textSizeThirdly(): T {
    return this.textSize(Dimen.textThirdly)
}

//--color--

fun <T : TextView> T.textColorTitle(): T {
    this.setTextColor(ColorX.textTitle)
    return this
}

fun <T : TextView> T.textColorPrimary(): T {
    this.setTextColor(ColorX.textPrimary)
    return this
}


fun <T : TextView> T.textColorSecondary(): T {
    this.setTextColor(ColorX.textSecondary)
    return this
}

fun <T : TextView> T.textColorSafe(): T {
    this.setTextColor(ColorX.safe)
    return this
}

fun <T : TextView> T.textColorRisk(): T {
    this.setTextColor(ColorX.risk)
    return this
}

fun <T : TextView> T.textColorAccent(): T {
    this.setTextColor(ColorX.accent)
    return this
}

fun <T : TextView> T.textColorPrimaryFade(): T {
    textColorList(ColorX.textPrimary) {
        lighted(ColorX.fade)
    }
    return this
}

fun <T : TextView> T.secondaryColorSize(): T {
    this.textColorSecondary().textSizeSecondary()
    return this
}

fun <T : TextView> T.primaryColorSize(): T {
    this.textColorPrimary().textSizePrimary()
    return this
}

fun TextView.stylePrimaryText(): TextView {
    gravityLeftCenter()
    primaryColorSize()
    textSizePrimary()
    return this
}

fun TextView.styleSecondaryText(): TextView {
    textSizeSecondary()
    textColorSecondary()
    gravityLeftCenter()
    return this
}

fun TextView.styleGridItemView(): TextView {
    padding(10)
    secondaryColorSize()

    gravityCenterHorizontal()
    compoundDrawablePadding = 5.dp
    minimumHeight = 40.dp
    minimumWidth = 40.dp
    return this
}

fun TextView.styleListItemView(): TextView {
    paddings(16, 10)
    stylePrimaryText()
    minimumHeight = 40.dp
    return this
}

fun ListView.styleDeftault() {
    this.cacheColorHint = 0
    this.selector = StateList.colorDrawable(Color.TRANSPARENT) {
        lighted(Color.LTGRAY)
    }
}

fun ImageView.styleDefault(): ImageView {
    adjustViewBounds = true
    scaleCenterCrop()
    return this
}

var TextView.textColorList: ColorStateList?
    get() = textColors
    set(value) = this.setTextColor(value)

//===========================

fun TextView.styleLinkAsButton() {
    padding(3)
    gravityCenter()
    textSizeSecondary()
    elevation = 0f
    textColorList = StateList.color(ColorX.accent) {
        lighted(ColorX.accentDark)
        disabled(ColorX.textDisabled)

    }
    val cl = StateList.color(Color.TRANSPARENT) {
        lighted(ColorX.divider)
        disabled(ColorX.backDisabled)
    }
    this.background = RippleDrawable(cl, ShapeRect(Color.TRANSPARENT, Dimen.buttonCorner).value, ShapeRect(ColorX.pageBackground, Dimen.buttonCorner).value)
    this.outlineProvider = OutlineRoundRect(Dimen.buttonCorner)
}
//styles = [normal, link, round-corner, outline ]


fun Button.styleDefault() {
    padding(3)
    textSizePrimary()
    textColorPrimary()
    stateListAnimator = null
    elevation = 4.dpf
}

fun Button.styleLink() {
    styleDefault()
    elevation = 0f
    textColorList = StateList.color(ColorX.accent) {
        lighted(ColorX.accentDark)
        disabled(ColorX.textDisabled)

    }
    val cl = StateList.color(Color.TRANSPARENT) {
        lighted(ColorX.divider)
        disabled(ColorX.backDisabled)
    }
    this.background = RippleDrawable(cl, ShapeRect(Color.TRANSPARENT, Dimen.buttonCorner).value, ShapeRect(ColorX.pageBackground, Dimen.buttonCorner).value)
    this.outlineProvider = OutlineRoundRect(Dimen.buttonCorner)
}


fun Button.styleCorner(colorNormal: Int, corner: Int = Dimen.buttonCorner) {
    styleDefault()
    textColorWhite()
    val cl = StateList.color(Color.TRANSPARENT) {
        lighted(colorNormal.darkColor)
        disabled(ColorX.backDisabled)
    }
    val ls = StateList.drawable {
        normal {
            ShapeRect(colorNormal, corner).value
        }
        disabled {
            ShapeRect(ColorX.backDisabled, corner).value
        }
    }
//    this.background = RippleDrawable(cl, ShapeRect(colorNormal, corner).value, ShapeRect(colorNormal, corner).value)
    this.background = RippleDrawable(cl, ls, ShapeRect(colorNormal, corner).value)
    this.outlineProvider = OutlineRoundRect(corner)
}

fun Button.styleOutline(colorBorer: Int, colorFill: Int = ColorX.pageBackground, corner: Int = Dimen.buttonCorner) {
    styleDefault()
    val cl = StateList.color(Color.TRANSPARENT) {
        lighted(colorBorer.darkColor)
        disabled(ColorX.backDisabled)
    }
    val ls = StateList.drawable {
        normal {
            Shapes.rect {
                fill(colorFill)
                corner(corner)
                stroke(2, colorBorer)
            }
        }
        disabled {
            Shapes.rect {
                fill(colorFill)
                corner(corner)
                stroke(2, ColorX.buttonBackDisabled)
            }
        }
    }
    val b = Shapes.rect {
        fill(colorBorer)
        corner(corner)
    }
    this.background = RippleDrawable(cl, ls, b)
    this.outlineProvider = OutlineRoundRect(corner)
}

//需要知道高度， 从layoutParams中取
fun Button.styleRound(colorNormal: Int) {
    styleDefault()
    textColorWhite()
    val pxValue = this.layoutParams.height / 2
    val r = px2dp(pxValue)
    this.background = rippleDrawable {
        rippleColors {
            normal(Color.TRANSPARENT)
            lighted(colorNormal.darkColor)
            disabled(ColorX.backDisabled)
        }
        content = StateList.drawable {
            normal {
                Shapes.rect {
                    fill(colorNormal)
                    corner(r)
                }
            }
            disabled {
                Shapes.rect {
                    fill(ColorX.buttonBackDisabled)
                    corner(r)
                }
            }
        }
        mask = ShapeRect(colorNormal, r).value
    }
    this.outlineProvider = OutlineRoundRect(r)
    clipToOutline = true
}

class RippleBuilder {
    lateinit var ripple: ColorStateList
    var content: Drawable? = null
    var mask: Drawable? = null

    fun ripple(block: () -> ColorStateList) {
        this.ripple = block()
    }

    fun rippleColors(block: ViewStateBuilder<Int>.() -> Unit) {
        this.ripple = StateList.color(block)
    }

    fun content(block: () -> Drawable) {
        this.content = block()
    }

    fun mask(block: () -> Drawable) {
        this.mask = block()
    }
}

fun rippleDrawable(block: RippleBuilder.() -> Unit): RippleDrawable {
    val b = RippleBuilder()
    b.block()
    return RippleDrawable(b.ripple, b.content, b.mask)
}


fun <T : CompoundButton> T.tintColors(normal: Int, checkedColor: Int): T {
    CompoundButtonCompat.setButtonTintList(this, StateList.color {
        normal(normal)
        checked(checkedColor)
    })
    return this
}