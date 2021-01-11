package dev.entao.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Size
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import dev.entao.appbase.dp
import dev.entao.theme.Space


open class RelItemView(context: Context) : RelativeLayout(context) {
    var positionBind: Int = 0
    var argInt: Int = 0
    var argString: String = ""

    init {
        needId()
        padding(Space.X, 5, Space.X, 5)
        backColorWhiteFade()
        this.minimumHeight = 50.dp
        this.layoutParams = Params.margin.widthFill.heightWrap
    }
}


class ImageTitleItemView(context: Context) : RelItemView(context) {
    private var size: Size = Size(40, 40)
    val imageView: ImageView = imageView(Params.relative.size(size.width, size.height).parentLeft.parentCenterY) {
        scaleCenterCrop()
    }
    val titleView: TextView = textView(Params.relative.wrap.parentCenterY.toRight(imageView).marginLeft(8)) {
        singleLine().ellipsizeEnd()
        primaryColorSize()
    }

    init {
        padding(10, 5, Space.X, 5)
    }


    var title: String
        get() = titleView.textS
        set(value) {
            titleView.textS = value
        }


    var image: Drawable?
        get() = this.imageView.drawable
        set(value) {
            this.imageView.setImageDrawable(value)
        }

    var imageSize: Size
        get() = this.size
        set(value) {
            this.size = value
            val lp = imageView.layoutParams
            lp.size(size.width, size.height)
            this.minimumHeight = size.height.dp
        }
}




/**
 * -----------------------------------------------
 * |     | title                          time   |
 * |image|                                       |
 * |     | msg                            status |
 * -----------------------------------------------
 */
class ImageText4ItemView(context: Context) : RelItemView(context) {
    private var size: Size = Size(44, 44)
    val imageView: ImageView = imageView(Params.relative.size(size.width, size.height).parentLeft.parentCenterY) {
        scaleCenterCrop()
    }
    val timeView: TextView = textView(Params.relative.wrap.parentTop.parentRight) {
        singleLine().ellipsizeEnd()
        secondaryColorSize()
    }
    val statusView: TextView = textView(Params.relative.wrap.parentBottom.parentRight) {
        singleLine().ellipsizeEnd()
        secondaryColorSize()
    }
    val titleView: TextView = textView(Params.relative.heightWrap.parentTop.toRight(imageView).toLeft(timeView).marginX(8)) {
        singleLine().ellipsizeEnd()
        primaryColorSize()
    }
    val msgView: TextView = textView(Params.relative.heightWrap.parentBottom.toRight(imageView).toLeft(statusView).marginX(8)) {
        singleLine().ellipsizeEnd()
        secondaryColorSize()
    }

    init {
        padding(10, 8, Space.X, 8)
    }

    var titleValue: String
        get() = titleView.textS
        set(value) {
            titleView.textS = value
        }
    var msgValue: String
        get() = msgView.textS
        set(value) {
            msgView.textS = value
        }
    var statusValue: String
        get() = statusView.textS
        set(value) {
            statusView.textS = value
        }

    var timeValue: String
        get() = timeView.textS
        set(value) {
            timeView.textS = value
        }


    var image: Drawable?
        get() = this.imageView.drawable
        set(value) {
            this.imageView.setImageDrawable(value)
        }

    var imageSize: Size
        get() = this.size
        set(value) {
            this.size = value
            val lp = imageView.layoutParams
            lp.size(size.width, size.height)
            this.minimumHeight = size.height.dp
        }
}

/**
 * -----------------------------------------------
 * | title                          time   |
 * |                                       |
 * | msg                            status |
 * -----------------------------------------------
 */
class Text4ItemView(context: Context) : RelItemView(context) {
    val timeView: TextView = textView(Params.relative.wrap.parentTop.parentRight) {
        singleLine().ellipsizeEnd()
        secondaryColorSize()
    }
    val statusView: TextView = textView(Params.relative.wrap.parentBottom.parentRight) {
        singleLine().ellipsizeEnd()
        secondaryColorSize()
    }
    val titleView: TextView = textView(Params.relative.parentLeft.parentTop.heightWrap.toLeft(timeView).marginRight(8).marginY(5)) {
        singleLine().ellipsizeEnd()
        primaryColorSize()
    }
    val msgView: TextView = textView(Params.relative.parentLeft.parentBottom.heightWrap.toLeft(statusView).marginRight(8).marginY(0)) {
        singleLine().ellipsizeEnd()
        secondaryColorSize()
    }

    init {
        padding(10, 8, Space.X, 8)
    }

    var titleValue: String
        get() = titleView.textS
        set(value) {
            titleView.textS = value
        }
    var msgValue: String
        get() = msgView.textS
        set(value) {
            msgView.textS = value
        }
    var statusValue: String
        get() = statusView.textS
        set(value) {
            statusView.textS = value
        }

    var timeValue: String
        get() = timeView.textS
        set(value) {
            timeView.textS = value
        }


}
