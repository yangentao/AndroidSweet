@file:Suppress("unused", "MemberVisibilityCanBePrivate", "FunctionName")

package dev.entao.imgloader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.LruCache
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import dev.entao.appbase.*
import dev.entao.views.*
import dev.entao.log.loge
import java.lang.ref.WeakReference

fun ImageView.loadURL(url: String, maxEdge: Int) {
    HttpImage(url).options { limit(maxEdge) }.display(this)
}

fun ImageView.loadURL(url: String, block: (ImageOption) -> Unit) {
    val h = HttpImage(url)
    block(h.option)
    h.display(this)
}

fun ImageView.loadRes(resId: Int, block: (ImageOption) -> Unit) {
    val h = ResImage(resId, block)
    setImageDrawable(h)
}

fun ImageView.loadUri(uri: Uri, block: (ImageOption) -> Unit) {
    val h = UriImage(uri, block)
    this.setImageDrawable(h)
}

fun TextView.topURL(url: String, boundsWidth: Int, boundsHeight: Int = boundsWidth, block: (ImageOption) -> Unit) {
    val httpImage = HttpImage(url)
    httpImage.option.boundWidth = boundsWidth
    httpImage.option.boundHeight = boundsHeight
    block(httpImage.option)
    httpImage.display(this) { v, d ->
        v as TextView
        if (d != null && d.bounds.width() == 0) {
            loge("警告:需要设置bounds.width/height")
        }
        v.topImage = d
    }
}

fun UriImage(uri: Uri, block: ImageOption.() -> Unit): Drawable? {
    val opt = ImageOption()
    opt.block()
    val b = Bmp.uri(uri, opt.limit, opt.quility)
    return b?.makeDrawable(opt)
}

fun ResImage(resId: Int, block: ImageOption.() -> Unit): Drawable {
    val opt = ImageOption()
    opt.block()
    val bmp = resId.resBitmap
    return bmp.limit(opt.limit).makeDrawable(opt)
}

private fun Bitmap.makeDrawable(option: ImageOption): Drawable {
    val v = when {
        option.circled -> {
            this.circled
        }
        option.corners > 0 -> {
            this.rounded(option.corners)
        }
        else -> {
            this.drawable
        }
    }
    if (option.boundWidth > 0 && option.boundHeight > 0) {
        v.setBounds(0, 0, option.boundWidth, option.boundHeight)
    }
    return v
}

//mem cache
object ImageCache {
    private val cache = object : LruCache<String, Bitmap>(6 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.rowBytes * value.height
        }
    }

    fun find(key: String): Bitmap? {
        return cache.get(key)
    }

    fun put(key: String, bmp: Bitmap) {
        cache.put(key, bmp)
    }

    fun remove(key: String): Bitmap? {
        return cache.remove(key)
    }
}


class HttpImage(val url: String) {
    val option = ImageOption()

    val keyString: String
        get() {
            return "${option.keyString}@$url"
        }

    fun localBitmap(): Bitmap? {
        val a = ImageCache.find(keyString)
        if (a != null) {
            return a
        }
        val file = FileLocalCache.find(url) ?: return null
        return Bmp.uri(file.uriLocal, option.limit, option.quility)
    }

    fun display(view: View, block: (View, Drawable?) -> Unit) {
        viewMap[view.requireId()] = url

        if (!option.forceDownload) {
            val b = localBitmap()
            if (b != null) {
                makeViewImage(b, view, block)
                return
            }
        }

        if (option.defaultImage != 0) {
            block(view, option.defaultImage.resBitmap.limit(option.limit).makeDrawable(option))
        }
        val weakView = WeakReference(view)
        bitmap { bmp ->
            Task.fore {
                val v = weakView.get()
                if (v != null) {
                    makeViewImage(bmp, v, block)
                }
            }
        }
    }

    fun bitmap(block: (Bitmap?) -> Unit) {
        if (option.forceDownload) {
            FileDownloader.download(url) {
                val b = Bmp.uri(it?.uriLocal, option.limit, option.quility)
                block(b)
            }
        } else {
            FileDownloader.retrive(url) {
                val b = Bmp.uri(it?.uriLocal, option.limit, option.quility)
                block(b)
            }
        }

    }

    private fun makeViewImage(bmp: Bitmap?, view: View, block: (View, Drawable?) -> Unit) {
        if (viewMap[view.requireId()] != this.url) {
            return
        }
        when {
            bmp != null -> {
                ImageCache.put(keyString, bmp)
                block(view, bmp.makeDrawable(option))
            }
            option.failedImage != 0 -> {
                block(view, option.failedImage.resBitmap.limit(option.limit).makeDrawable(option))
            }
            else -> {
                block(view, null)
            }
        }
    }


    fun display(imageView: ImageView) {
        display(imageView) { v, d ->
            v as ImageView
            v.setImageDrawable(d)
        }
    }

    fun rightImage(textView: TextView, boundsWidth: Int, boundsHeight: Int = boundsWidth) {
        display(textView) { v, d ->
            v as TextView
            d?.sized(boundsWidth, boundsHeight)
            v.rightImage = d
        }
    }

    fun leftImage(textView: TextView, boundsWidth: Int, boundsHeight: Int = boundsWidth) {
        display(textView) { v, d ->
            v as TextView
            d?.sized(boundsWidth, boundsHeight)
            v.leftImage = d
        }
    }

    fun topImage(textView: TextView, boundsWidth: Int, boundsHeight: Int = boundsWidth) {
        display(textView) { v, d ->
            v as TextView
            d?.sized(boundsWidth, boundsHeight)
            v.topImage = d
        }
    }

    fun options(block: ImageOption.() -> Unit): HttpImage {
        option.block()
        return this
    }


    companion object {
        private val viewMap: HashMap<Int, String> = HashMap()
        fun batch(ls: List<String>, optBlock: ImageOption.() -> Unit, callback: (List<Bitmap>) -> Unit) {
            val bls = ArrayList<Bitmap?>()
            if (ls.isEmpty()) {
                callback(emptyList())
                return
            }
            ls.forEach { url ->
                HttpImage(url).options(optBlock).bitmap { bmp ->
                    bls += bmp
                    if (bls.size == ls.size) {
                        callback(bls.filterNotNull())
                    }
                }
            }
        }
    }
}