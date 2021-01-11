@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.android.material.switchmaterial.SwitchMaterial
import dev.entao.appbase.*
import dev.entao.theme.ColorX

/**
 * Created by entaoyang@163.com on 16/6/4.
 */


//width:60, height:30
class SwitchButton(context: Context) : AppCompatCheckBox(context) {

    init {
        this.compoundDrawablePadding = 0
        this.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            this.post {
                resetImage()
            }
        }
        this.setOnCheckedChangeListener { buttonView, isChecked ->
            this.post {
                resetImage()
            }
        }
        this.layoutParams = Params.margin.width(WIDTH).height(HEIGHT)
    }


    private fun uncheckedImage(w: Int, h: Int): Drawable {
        val back = ShapeRect(ColorX.backDisabled, h / 2).size(w, h).value
        val round = ShapeOval().fill(Color.WHITE).size(h - 2).value
        val ld = LayerDrawable(arrayOf(back, round))
        val inner = 1.dp
        ld.setLayerInset(1, inner * 2, inner, (w - h).dp - 2 * inner, inner)

        return ld
    }

    private fun checkedImage(w: Int, h: Int): Drawable {
        val back = ShapeRect(ColorX.green, h / 2).size(w, h).value
        val round = ShapeOval().fill(Color.WHITE).size(h - 2).value
        val ld = LayerDrawable(arrayOf(back, round))
        val inner = 1.dp
        ld.setLayerInset(1, (w - h).dp - inner, inner, inner * 2, inner)
        return ld
    }

    private fun switchDraw(w: Int, h: Int): Drawable {
        val a = uncheckedImage(w, h)
        val b = checkedImage(w, h)
        return StateList.drawable(a) {
            checked(b)
        }
    }


    private fun resetImage() {
        this.buttonDrawable = switchDraw(px2dp(this.width), px2dp(this.height))
    }

    companion object {
        const val WIDTH = 60
        const val HEIGHT = 30
    }
}

fun ViewGroup.switchButton(block: SwitchButton.() -> Unit): SwitchButton {
    return append(block)
}

fun ViewGroup.switchMeterial(block: SwitchMaterial.() -> Unit): SwitchMaterial {
    return append(block)
}

fun <T : Switch> T.themeSwitch(): T {
    this.thumbTextPadding = 10.dp

    val w1 = 30
    val h1 = 30

    this.thumbDrawable = StateList.drawable {
        normal {
            ShapeRect(0xFFCCCCCCL.argb, h1 / 2).size(w1, h1).value
        }
        checked {
            ShapeRect(0xFF4A90E2L.argb, h1 / 2).size(w1, h1).value
        }
        disabled {
            ShapeRect(ColorX.backDisabled, h1 / 2).stroke(1, ColorX.borderGray).size(w1, h1).value
        }
    }

    val w = 50
    val h = 30

    this.trackDrawable = StateList.drawable {
        normal {
            ShapeRect(Color.WHITE, h / 2).stroke(1, ColorX.borderGray).size(w, h).value
        }
        checked {
            ShapeRect(ColorX.green, h / 2).size(w, h).value
        }
        disabled {
            ShapeRect(ColorX.backDisabled, h / 2).stroke(1, Color.WHITE).size(w, h).value
        }
    }
    return this
}