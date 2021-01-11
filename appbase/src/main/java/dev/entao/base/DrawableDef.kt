package dev.entao.base

import android.graphics.Color
import android.graphics.drawable.Drawable
import dev.entao.appbase.ShapeRect
import dev.entao.appbase.StateList
import dev.entao.theme.ColorX
import dev.entao.theme.Dimen
import dev.entao.app.R
/**
 * Created by entaoyang@163.com on 2016-07-23.
 */

object DrawableDef {
    val CheckBox: Drawable
        get() = StateList.resDrawable(R.mipmap.yet_checkbox) {
            checked(R.mipmap.yet_checkbox_checked)
        }


    fun buttonWhite(corner: Int = Dimen.buttonCorner): Drawable {
        return buttonColor(Color.rgb(230, 230, 230), corner)
    }

    fun buttonColor(color: Int, corner: Int = Dimen.buttonCorner): Drawable {
        val normal = ShapeRect(color, corner).value
        val pressed = ShapeRect(ColorX.fade, corner).value
        val enableFalse = ShapeRect(ColorX.backDisabled, corner).value
        return StateList.drawable(normal) {
            pressed(pressed)
            disabled(enableFalse)
        }
    }


}