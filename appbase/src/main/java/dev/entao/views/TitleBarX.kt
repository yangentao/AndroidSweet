package dev.entao.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import dev.entao.appbase.*
import dev.entao.base.BlockUnit
import dev.entao.theme.ColorX

import dev.entao.app.R

private var incN = 0
private fun autoTitleBarKey(): String {
    incN++
    return "BarItem$incN"
}

abstract class TitleBarXItem(val context: Context) {
    var key: String = autoTitleBarKey()

    abstract val view: View
}

class TitleBarXTextItem(context: Context) : TitleBarXItem(context) {
    override val view: TextView = TextView(context).apply {
        textColorWhite()
        textSizePrimary()
        gravityCenter()
        padding(5, 1)
        backColorTransFade()
        minimumWidth = TitleBarX.HEIGHT.dp
        layoutParams = Params.linear.widthWrap.heightFill
    }
}

open class TitleBarXImageItem(context: Context) : TitleBarXItem(context) {
    var autoTint = true
    override val view: ImageView = ImageView(context).apply {
        padding(0, 10)
        backColorTransFade()
        scaleCenterInside()
        layoutParams = Params.linear.width(TitleBarX.HEIGHT).heightFill
    }

    fun res(@DrawableRes resId: Int) {
        val d = resId.resDrawable.mutate()
        this.drawable(d)
    }

    fun drawable(d: Drawable?) {
        if (d == null) {
            view.setImageDrawable(null)
        } else {
            val d = if (autoTint) {
                d.tintedWhite.sized(TitleBarX.ImgSize)
            } else {
                d.sized(TitleBarX.ImgSize)
            }
            view.setImageDrawable(d)
        }
    }
}

class TitleBarXBack(context: Context) : TitleBarXImageItem(context) {
    init {
        key = TitleBarX.BACK
        res(R.mipmap.yet_back)
    }
}

class TitleBarXMenu(context: Context) : TitleBarXImageItem(context) {
    val children = ArrayList<TitleBarXMenuItem>()

    init {
        key = TitleBarX.MENU
        res(R.mipmap.yet_menu)
    }

    fun text(text: String, block: BlockUnit) {
        val item = TitleBarXMenuItem(context)
        item.text(text)
        item.onClick = block
        children += item
    }

    fun iconText(@DrawableRes icon: Int, text: String, block: BlockUnit) {
        val item = TitleBarXMenuItem(context)
        if (icon == 0) {
            item.iconText(ColorDrawable(Color.TRANSPARENT), text)
        } else {
            item.iconText(icon.resDrawable, text)
        }
        item.onClick = block
        children += item
    }

    fun item(block: TitleBarXMenuItem.() -> Unit) {
        val item = TitleBarXMenuItem(context)
        children += item
        item.block()
    }
}

class TitleBarXMenuItem(context: Context) : TitleBarXItem(context) {
    override val view: TextView = TextView(context).apply {
        textColorWhite()
        textSizePrimary()
        gravityLeftCenter()
        padding(10, 5, 20, 5)
        backColorTransFade()
        singleLine()
        layoutParams = Params.linear.widthFill.height(45)
    }

    fun text(text: String) {
        this.view.text = text
    }

    fun iconText(icon: Drawable?, text: String) {
        this.view.text = text
        val d = icon?.mutate()?.tintedWhite ?: Color.TRANSPARENT.colorDrawable
        view.compoundDrawablePadding = 10.dp
        view.setCompoundDrawables(d.sized(TitleBarX.ImgSize), null, null, null)
    }

    var onClick: BlockUnit = {}

}

@SuppressLint("ViewConstructor")
class TitleBarX(context: Context) : RelativeLayout(context) {
    private val leftCmds = ArrayList<TitleBarXItem>()
    private val rightCmds = ArrayList<TitleBarXItem>()
    val titleView: TextView
    val rightLinear: LinearLayout
    val leftLinear: LinearLayout


    var popWindow: PopupWindow? = null

    init {
        backColor(ColorX.theme)
        this.elevation = 5.dpf
        leftLinear = this.linearLayoutH {
            relativeParams {
                widthWrap.heightFill.margins(5, 0).parentCenterY.parentLeft
            }
        }
        rightLinear = this.linearLayoutH {
            relativeParams {
                widthWrap.heightFill.margins(5, 0).parentCenterY.parentRight
            }
        }
        titleView = this.textView {
            relativeParams {
                parentCenter.widthWrap.heightFill
            }
            gravityCenter()
            paddingX(20)
            textColorWhite()
            textSizeTitle()
        }
    }


    fun title(text: String) {
        titleView.text = text
    }

    fun clickTitle(block: BlockUnit) {
        titleView.onClick(block)
    }

    fun text(text: String, block: BlockUnit): TitleBarXTextItem {
        val item = TitleBarXTextItem(context)
        item.view.text = text
        item.view.onClick(block)
        addRight(item)
        return item
    }

    fun image(@DrawableRes resId: Int, block: BlockUnit): TitleBarXImageItem {
        val item = TitleBarXImageItem(context)
        item.res(resId)
        item.view.onClick(block)
        addRight(item)
        return item
    }

    fun showBack() {
        removeIf { it == BACK }
        val item = TitleBarXBack(context)
        addLeft(item)
    }

    fun showBack(block: BlockUnit) {
        removeIf { it == BACK }
        val item = TitleBarXBack(context)
        item.view.onClick(block)
        addLeft(item)
    }

    fun first(block: (String) -> Boolean): TitleBarXItem? {
        for (item in leftCmds) {
            if (block(item.key)) {
                return item
            }
        }
        for (item in rightCmds) {
            if (block(item.key)) {
                return item
            }
        }
        return null
    }

    fun removeIf(block: (String) -> Boolean) {
        for (item in leftCmds) {
            if (block(item.key)) {
                item.view.removeFromParent()
                leftCmds.remove(item)
                break
            }
        }
        for (item in rightCmds) {
            if (block(item.key)) {
                item.view.removeFromParent()
                rightCmds.remove(item)
                break
            }
        }
    }

    fun removeAllIf(block: (String) -> Boolean) {
        val ls = ArrayList<TitleBarXItem>()
        for (item in leftCmds) {
            if (block(item.key)) {
                item.view.removeFromParent()
                ls.add(item)
            }
        }
        for (a in ls) {
            leftCmds.remove(a)
        }
        ls.clear()
        for (item in rightCmds) {
            if (block(item.key)) {
                item.view.removeFromParent()
                ls.add(item)
            }
        }
        for (b in ls) {
            rightCmds.remove(b)
        }
        ls.clear()
    }

    fun addLeft(item: TitleBarXItem) {
        if (item !in this.leftCmds) {
            this.leftCmds.add(0, item)
            item.view.removeFromParent()
            if (item.view.layoutParams !is LinearLayout.LayoutParams) {
                item.view.layoutParams = Params.linear.heightFill.widthWrap
            }
            this.leftLinear.addView(item.view, 0)
        }
    }

    fun addRight(item: TitleBarXItem) {
        if (item !in this.rightCmds) {
            this.rightCmds.add(item)
            item.view.removeFromParent()
            if (item.view.layoutParams !is LinearLayout.LayoutParams) {
                item.view.layoutParams = Params.linear.heightFill.widthWrap
            }
            this.rightLinear.addView(item.view)
        }
    }

    operator fun invoke(block: TitleBarX.() -> Unit) {
        this.block()
    }


    fun menu(block: TitleBarXMenu.() -> Unit) {
        val item = TitleBarXMenu(context)
        item.view.onClick {
            popMenu(item)
        }
        addRight(item)
        item.block()
    }

    private fun popMenu(item: TitleBarXMenu) {
        val p = PopupWindow(context).apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(0))
        }

        val popRootView = LinearLayout(context).apply {
            vertical()
            divider()
            padding(0)
            backRect {
                corners(0, 0, 2, 2)
                fill(ColorX.theme)
            }
            minimumWidth = 150.dp
        }

        for (c in item.children) {
            popRootView.addView(c.view)
            c.view.setOnClickListener {
                popWindow?.dismiss()
                Task.fore {
                    c.onClick()
                }
            }
        }
        p.contentView = popRootView
        popWindow = p

        p.setOnDismissListener {
            (popWindow?.contentView as? ViewGroup)?.removeAllViews()
            popWindow = null
        }
        p.showAsDropDown(item.view, 0, 1)
    }


    companion object {
        const val BACK = "back"
        const val MENU = "menu"
        const val ImgSize = 24
        const val HEIGHT = 50// dp, android Toolbar高度是56
    }
}

