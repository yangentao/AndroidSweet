@file:Suppress("unused")

package dev.entao.appbase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * Created by entaoyang@163.com on 2016-10-16.
 */

val Int.colorDrawable: ColorDrawable get() = ColorDrawable(this)

@Suppress("DEPRECATION")
val Int.resColor: Int
    get() = ResourcesCompat.getColor(App.resource, this, App.theme)

val Int.resDrawable: Drawable
    get() = ResourcesCompat.getDrawable(App.resource, this, App.theme)!!

val Int.resBitmap: Bitmap
    get() {
        return BitmapFactory.decodeResource(App.resource, this)
    }


val Int.resString: String
    get() {
        return App.resource.getString(this)
    }

fun Int.resStrArgs(vararg args: Any): String {
    return App.resource.getString(this, *args)
}




