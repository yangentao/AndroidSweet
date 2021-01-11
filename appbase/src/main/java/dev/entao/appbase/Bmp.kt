package dev.entao.appbase

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import dev.entao.base.closeSafe
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.math.roundToInt

object Bmp {

    fun uri(uri: Uri?): Bitmap? {
        if (uri == null) {
            return null
        }
        try {
            val a = App.openStream(uri) ?: return null
            return decodeStream(a)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    //图片高或宽不超过maxEdge
    fun uri(uri: Uri?, maxEdge: Int, config: Bitmap.Config): Bitmap? {
        if (uri == null) {
            return null
        }
        try {
            if (maxEdge < 1) {
                val a = App.openStream(uri) ?: return null
                return decodeStream(a, 1, config)
            }
            val size = sizeOf(uri) ?: return null
            val n = if (size.maxEdge() > maxEdge) {
                (size.maxEdge() * 1.0f / maxEdge).roundToInt()
            } else {
                1
            }
            val a = App.openStream(uri) ?: return null
            return decodeStream(a, n, config)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    //高或宽不超过maxEdge
    fun file(imageFile: File?, maxEdge: Int, config: Bitmap.Config): Bitmap? {
        if (null == imageFile || !imageFile.exists()) {
            return null
        }
        try {
            if (maxEdge < 1) {
                return decodeStream(FileInputStream(imageFile), 1, config)
            }
            val size = sizeOf(imageFile) ?: return null
            val n = if (size.maxEdge() > maxEdge) {
                (size.maxEdge() * 1.0f / maxEdge).roundToInt()
            } else {
                1
            }
            return decodeStream(FileInputStream(imageFile), n, config)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }


    fun line(width: Int, height: Int, color: Int): Bitmap {
        val target = Bitmap.createBitmap(width, height, Config.ARGB_8888)
        target.density = DisplayMetrics.DENSITY_HIGH
        val canvas = Canvas(target)
        canvas.drawColor(color)
        return target
    }

    // 高和宽较最小的值做直径,
    fun tint(res: Int, color: Int): Bitmap {
        return res.resBitmap.tint(color)
    }


    fun tintLight(@DrawableRes res: Int, normalColor: Int, lightColor: Int): StateListDrawable {
        val bmp = res.resBitmap
        val d = bmp.tint(normalColor).drawable
        val d2 = bmp.tint(lightColor).drawable
        return StateList.lightDrawables(d, d2)
    }

    @Throws(FileNotFoundException::class)
    fun sizeOf(uri: Uri): MySize? {
        val a = App.openStream(uri) ?: return null
        return sizeOfStream(a)
    }

    @Throws(FileNotFoundException::class)
    fun sizeOf(file: File): MySize? {
        return sizeOfStream(FileInputStream(file))
    }

    //会关闭流
    private fun sizeOfStream(inputStream: InputStream): MySize? {
        try {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, opts)
            return MySize(opts.outWidth, opts.outHeight)
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            inputStream.closeSafe()
        }
        return null
    }


    //会关闭流
    @Suppress("DEPRECATION")
    fun decodeStream(inputStream: InputStream, inSampleSize: Int, config: Bitmap.Config): Bitmap? {
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = false
        opts.inSampleSize = inSampleSize
        opts.inPreferredConfig = config
        opts.inInputShareable = true
        opts.inPurgeable = true
        val bmp = BitmapFactory.decodeStream(inputStream, null, opts)
        inputStream.closeSafe()
        return bmp
    }

    private fun decodeStream(inputStream: InputStream): Bitmap? {
        val bmp = BitmapFactory.decodeStream(inputStream)
        inputStream.closeSafe()
        return bmp
    }


    fun compressJpg(from: Uri, maxEdge: Int): File? {
        val bmp = uri(from, maxEdge, Config.ARGB_8888)
        if (bmp != null) {
            val tofile = AppFile.tempFile(".jpg")
            val b = bmp.saveJpg(tofile)
            if (b && tofile.exists()) {
                return tofile
            } else {
                tofile.delete()
            }
        }
        return null
    }

    fun compressPng(from: Uri, maxEdge: Int): File? {
        val bmp = uri(from, maxEdge, Config.ARGB_8888)
        if (bmp != null) {
            val tofile = AppFile.tempFile(".png")
            val b = bmp.savePng(tofile)
            if (b && tofile.exists()) {
                return tofile
            } else {
                tofile.delete()
            }
        }
        return null
    }


}
