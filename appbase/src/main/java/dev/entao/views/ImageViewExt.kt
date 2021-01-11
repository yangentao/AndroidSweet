@file:Suppress("unused")

package dev.entao.views

import android.graphics.Color
import android.widget.ImageView
import androidx.annotation.DrawableRes
import dev.entao.appbase.*
import dev.entao.theme.ColorX

/**
 * Created by entaoyang@163.com on 16/3/13.
 */

fun <T : ImageView> T.scaleCenter(): T {
    this.scaleType = ImageView.ScaleType.CENTER
    return this
}

fun <T : ImageView> T.scaleCenterInside(): T {
    this.scaleType = ImageView.ScaleType.CENTER_INSIDE
    return this
}

fun <T : ImageView> T.scaleCenterCrop(): T {
    this.scaleType = ImageView.ScaleType.CENTER_CROP
    return this
}


fun <T : ImageView> T.scaleFitXY(): T {
    this.scaleType = ImageView.ScaleType.FIT_XY
    return this
}

fun <T : ImageView> T.scaleFitCenter(): T {
    this.scaleType = ImageView.ScaleType.FIT_CENTER
    return this
}

fun <T : ImageView> T.scaleFitStart(): T {
    this.scaleType = ImageView.ScaleType.FIT_START
    return this
}

fun <T : ImageView> T.scaleFitEnd(): T {
    this.scaleType = ImageView.ScaleType.FIT_END
    return this
}

fun <T : ImageView> T.tintWhite(@DrawableRes resId: Int): T {
    this.setImageDrawable(resId.resDrawable.tintedWhite)
    return this
}

fun <T : ImageView> T.resSizedWhite(@DrawableRes resId: Int, size: Int): T {
    scaleCenterInside()
    val b = resId.resBitmap
    val bb = b.limit(size.dp).tint(Color.WHITE)
    this.setImageBitmap(bb)
    return this
}

fun <T : ImageView> T.resSizedTheme(@DrawableRes resId: Int, size: Int): T {
    scaleCenterInside()
    val b = resId.resBitmap
    val bb = b.limit(size.dp).tint(ColorX.theme)
    this.setImageBitmap(bb)
    return this
}