@file:Suppress("unused")

package dev.entao.theme

import android.graphics.Color
import android.graphics.drawable.StateListDrawable
import androidx.annotation.ColorRes
import dev.entao.app.R
import dev.entao.appbase.*
import dev.entao.base.Hex

object ColorX {
    fun colorOf(@ColorRes resId: Int): Int {
        return App.color(resId)
    }

    val theme: Int get() = colorOf(R.color.titleBar)
    val titleBar: Int get() = colorOf(R.color.titleBar)
    val textPrimary: Int get() = colorOf(R.color.textPrimary)
    val textSecondary: Int get() = colorOf(R.color.textSecondary)
    val pageBackground: Int get() = colorOf(R.color.pageBackground)
    val dialogBackground: Int get() = colorOf(R.color.dialogBackground)
    val divider: Int get() = colorOf(R.color.divider)
    val dialogTitleBar: Int get() = colorOf(R.color.dialogTitleBar)
    val textTitle: Int get() = colorOf(R.color.textTitle)


    val fade: Int get() = colorOf(R.color.fadeColor)

    val accent: Int get() = colorOf(R.color.accent)
    val accentDark: Int get() = colorOf(R.color.accentDark)
    val risk: Int get() = colorOf(R.color.risk)
    val riskDark: Int get() = colorOf(R.color.riskDark)
    val safe: Int get() = colorOf(R.color.safe)
    val safeDark: Int get() = colorOf(R.color.safeDark)

    val textDisabled: Int get() = colorOf(R.color.textDisabled)
    val backDisabled: Int get() = colorOf(R.color.backDisabled)
    val buttonBackDisabled: Int get() = colorOf(R.color.buttonBackDisabled)

    val editBorder: Int get() = colorOf(R.color.editBorder)
    val editBorderFocus: Int get() = colorOf(R.color.editBorderFocus)


    val itemFadeBackground: StateListDrawable
        get() = StateList.colorDrawable(pageBackground) {
            lighted(fade)
        }


    //============
    var red: Int = 0xAD1919.rgb
    var redDark: Int = 0x981414.rgb
    var redLight: Int = 0xD80909.rgb
    var green: Int = 0x2BA62E.rgb
    var greenDark: Int = 0x167724.rgb
    var greenLight: Int = 0x24CD1B.rgb
    var blue: Int = 0x3188BA.rgb
    var blueDark: Int = 0x0B5684.rgb
    var blueLight: Int = 0x11A0E0.rgb
    var cyan: Int = 0x41A993.rgb
    var cyanDark: Int = 0x0F8169.rgb
    var cyanLight: Int = 0x23D1AC.rgb


    var EditFocus = 0xFF38C4B0.argb
    var borderGray: Int = Color.LTGRAY

    var backGray: Int = 0xFFDDDDDD.argb

    const val TRANS = Color.TRANSPARENT

    var Progress: Int = greenLight

    var Unselected = 0xFFAAAAAA.argb

    //0xff8800 --> "#ff8800"
    fun toStringColor(color: Int): String {
        val a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        if (a == 0xff) {
            return "#" + Hex.encodeByte(r) + Hex.encodeByte(g) + Hex.encodeByte(b)
        }
        return "#" + Hex.encodeByte(a) + Hex.encodeByte(r) + Hex.encodeByte(g) + Hex.encodeByte(b)
    }

}