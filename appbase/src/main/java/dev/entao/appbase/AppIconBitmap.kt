package dev.entao.appbase

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build


fun getAppIcon(pm: PackageManager, pkg: String): Bitmap? {
    if (Build.VERSION.SDK_INT < 26) {
        try {
            val d = pm.getApplicationIcon(pkg)
            if (d is BitmapDrawable) {
                return d.bitmap
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    } else {
        return getAppIcon2(pm, pkg)
    }

}

@TargetApi(Build.VERSION_CODES.O)
fun getAppIcon2(pm: PackageManager, pkg: String): Bitmap? {
    try {
        val drawable = pm.getApplicationIcon(pkg)
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        if (drawable is AdaptiveIconDrawable) {
            val layerDrawable = LayerDrawable(arrayOf(drawable.background, drawable.foreground))
            val width = layerDrawable.intrinsicWidth
            val height = layerDrawable.intrinsicHeight
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
            layerDrawable.draw(canvas)
            return bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}