@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.imgloader

import android.graphics.Bitmap
import dev.entao.base.ImageDef

class ImageOption {
    var quility: Bitmap.Config = Bitmap.Config.RGB_565
    var limit: Int = 720
    var corners: Int = 0
    var circled: Boolean = false

    var boundWidth: Int = 0
    var boundHeight: Int = 0

    var forceDownload: Boolean = false

    //加载失败的图片
    var failedImage: Int = ImageDef.imageMiss

    //默认的图片, 下载前
    var defaultImage: Int = ImageDef.imageMiss

    val keyString: String
        get() = "$quility:$limit"

    fun bounds(w: Int, h: Int): ImageOption {
        this.boundWidth = w
        this.boundHeight = h
        return this
    }

    fun portrait(): ImageOption {
        limit256().onFailed(ImageDef.portrait).onDefault(ImageDef.portrait)
        quility8888()
        return this
    }


    fun forceDownload(): ImageOption {
        this.forceDownload = true
        return this
    }


    fun limit256(): ImageOption {
        return limit(256)
    }


    fun limit720(): ImageOption {
        return limit(720)
    }


    fun limit(n: Int): ImageOption {
        limit = n
        return this
    }

    fun quility8888(): ImageOption {
        quility = Bitmap.Config.ARGB_8888
        return this
    }

    fun quility565(): ImageOption {
        quility = Bitmap.Config.RGB_565
        return this
    }

    fun onFailed(id: Int): ImageOption {
        failedImage = id
        return this
    }

    fun onDefault(id: Int): ImageOption {
        defaultImage = id
        return this
    }
}