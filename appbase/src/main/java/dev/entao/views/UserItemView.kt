package dev.entao.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import dev.entao.base.ImageDef

class UserItemView(context: Context) : RelativeLayout(context) {

    val portraitView: ImageView
    val nameView: TextView
    val statusView: TextView

    init {
        portraitView = imageView(Params.relative.parentLeft.parentCenterY.size(64).margins(15)) {
            scaleCenterCrop()
            setImageResource(ImageDef.portrait)
        }

        nameView = textView(Params.relative.toRight(portraitView).parentTop.wrap.margins(0, 20, 0, 10)) {
            textSizeTitle()
            textColorPrimary()
        }
        statusView = textView(Params.relative.toRight(portraitView).below(nameView).wrap) {
            textSizePrimary()
            textColorSecondary()
        }

        imageView(RParam.parentRight.parentCenterY.size(14).marginRight(10)) {
            setImageResource(ImageDef.more)
        }
    }

    fun bindValues(name: String, status: String) {
        this.nameView.text = name
        this.statusView.text = status
    }

    fun portrait(resId: Int) {
        portraitView.setImageResource(resId)
    }

    fun portrait(d: Drawable) {
        portraitView.setImageDrawable(d)
    }
}