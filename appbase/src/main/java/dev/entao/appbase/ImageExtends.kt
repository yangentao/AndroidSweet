@file:Suppress("unused", "FunctionName")

package dev.entao.appbase

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import dev.entao.base.closeSafe
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Created by entang@163.com on 2018-03-06.
 */


val Bitmap.circled: Drawable
    get() {
        val d = RoundedBitmapDrawableFactory.create(App.resource, this)
        d.isCircular = true
        return d
    }
val Bitmap.rounded: Drawable
    get() {
        return this.rounded(8)
    }

fun Bitmap.rounded(corner: Int): Drawable {
    val d = RoundedBitmapDrawableFactory.create(App.resource, this)
    d.cornerRadius = corner.dpf
    return d
}

val Bitmap.drawable: BitmapDrawable
    get() {
        return BitmapDrawable(App.resource, this)
    }

fun Bitmap.tint(color: Int): Bitmap {
    val w = this.getScaledWidth(App.resource.displayMetrics)
    val h = this.getScaledHeight(App.resource.displayMetrics)
    val target = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(target)
    val paint = Paint()
    paint.color = color
    val rect = RectF(0f, 0f, w.toFloat(), h.toFloat())
    canvas.drawRect(rect, paint)
    paint.isAntiAlias = true
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    val rect2 = Rect(0, 0, w, h)
    canvas.drawBitmap(this, rect2, rect2, paint)
    return target
}

//圆,  高和宽较最小的值做直径,
fun Bitmap.oval(): Bitmap {
    val w = this.getScaledWidth(App.resource.displayMetrics)
    val h = this.getScaledHeight(App.resource.displayMetrics)
    val paint = Paint()
    paint.isAntiAlias = true

    val d = Math.min(w, h)
    val corner = d / 2
    val target = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(target)
    val rect = RectF(0f, 0f, d.toFloat(), d.toFloat())
    canvas.drawRoundRect(rect, corner.toFloat(), corner.toFloat(), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, (-(w - d) / 2).toFloat(), (-(h - d) / 2).toFloat(), paint)
    return target
}

//高宽的一半做圆角, 保持高宽比
fun Bitmap.roundBmp(): Bitmap {
    val w = this.getScaledWidth(App.resource.displayMetrics)
    val h = this.getScaledHeight(App.resource.displayMetrics)
    val paint = Paint()
    paint.isAntiAlias = true
    val target = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(target)
    val rect = RectF(0f, 0f, w.toFloat(), h.toFloat())
    canvas.drawRoundRect(rect, (w / 2).toFloat(), (h / 2).toFloat(), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return target
}

//圆角bitmap, 保持高宽比
fun Bitmap.roundBmp(cornerDp: Int): Bitmap {
    val corner = cornerDp.dp.toFloat()
    val w = this.getScaledWidth(App.resource.displayMetrics)
    val h = this.getScaledHeight(App.resource.displayMetrics)
    val paint = Paint()
    paint.isAntiAlias = true
    val target = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(target)

    val rect = RectF(0f, 0f, w.toFloat(), h.toFloat())
    canvas.drawRoundRect(rect, corner, corner, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return target
}

// 高和宽较最小的值做直径,  圆角正方形
fun Bitmap.roundSqure(cornerDp: Int): Bitmap {
    val corner = cornerDp.dp.toFloat()
    val w = this.getScaledWidth(App.resource.displayMetrics)
    val h = this.getScaledHeight(App.resource.displayMetrics)
    val paint = Paint()
    paint.isAntiAlias = true

    val d = Math.min(w, h)
    val target = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(target)
    val rect = RectF(0f, 0f, d.toFloat(), d.toFloat())
    canvas.drawRoundRect(rect, corner, corner, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, (-(w - d) / 2).toFloat(), (-(h - d) / 2).toFloat(), paint)
    return target
}


fun Bitmap.savePng(saveTo: File): Boolean {
    return savePng(FileOutputStream(saveTo))
}

fun Bitmap.savePng(saveTo: OutputStream): Boolean {
    try {
        return this.compress(Bitmap.CompressFormat.PNG, 100, saveTo)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        saveTo.closeSafe()
    }
    return false
}

fun Bitmap.saveJpg(saveTo: File): Boolean {
    return saveJpg(FileOutputStream(saveTo))
}

fun Bitmap.saveJpg(saveTo: OutputStream): Boolean {
    try {
        return this.compress(Bitmap.CompressFormat.JPEG, 100, saveTo)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        saveTo.closeSafe()
    }
    return false
}


fun Bitmap.rotate(degree: Int): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}


fun Bitmap.scaleTo(newWidth: Int, newHeight: Int): Bitmap {
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    return scale(scaleWidth, scaleHeight)
}

fun Bitmap.scale(f: Float): Bitmap {
    return this.scale(f, f)
}

fun Bitmap.scale(fWidth: Float, fHeight: Float): Bitmap {
    val matrix = Matrix()
    matrix.postScale(fWidth, fHeight)
    return  Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

//限制最大的高宽, 等比例缩放, 比如, 原图 300 * 400, limi(200,200)将会将图片变为原来的1/2, 150* 200
fun Bitmap.limit(maxWidth: Int, maxHeight: Int = maxWidth): Bitmap {
    if (maxWidth <= 0 || maxHeight <= 0) {
        return this
    }
    if (width < maxWidth && height < maxHeight) {
        return this
    }
    val fw = if (width > maxWidth) {
        maxWidth * 1.0 / width
    } else 1.0
    val fh = if (height > maxHeight) {
        maxHeight * 1.0 / height
    } else 1.0
    val f = Math.min(fw, fh).toFloat()
    return scale(f, f)
}


fun Drawable.sizeW(newWidth: Int): Drawable {
    val h = this.intrinsicHeight
    val w = this.intrinsicWidth
    if (newWidth == 0 || w == 0 || w == newWidth) {
        return this
    }
    this.setBounds(0, 0, newWidth.dp, h * newWidth.dp / w)
    return this
}

fun Drawable.sizeH(newHeight: Int): Drawable {
    val h = this.intrinsicHeight
    val w = this.intrinsicWidth
    if (newHeight == 0 || h == 0 || h == newHeight) {
        return this
    }
    this.setBounds(0, 0, w * newHeight.dp / h, newHeight.dp)
    return this
}

fun Drawable.limited(maxEdge: Int): Drawable {
    val h = this.intrinsicHeight
    val w = this.intrinsicWidth
    if (w > maxEdge || h > maxEdge) {
        val a = maxEdge.dp
        if (w > h) {
            this.setBounds(0, 0, a, h * a / w)
        } else {
            this.setBounds(0, 0, w * a / h, a)
        }
    }
    return this
}

fun Drawable.sized(w: Int, h: Int = w): Drawable {
    this.setBounds(0, 0, w.dp, h.dp)
    return this
}

val Drawable.tintedWhite: Drawable
    get() {
        return this.tinted(Color.WHITE)
    }

fun Drawable.tinted(color: Int): Drawable {
    val dc = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTint(dc, color)
    return dc
}

fun Drawable.tinted(color: Int, light: Int): Drawable {
    val dc = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTintList(dc, StateList.lightColors(color, light))
    return dc
}

fun Drawable.tintedList(color: Int, light: Int): Drawable {
    val dc = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTintList(dc, StateList.lightColors(color, light))
    return dc
}