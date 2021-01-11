package dev.entao.pages

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.widget.PopupWindowCompat
import dev.entao.appbase.*
import dev.entao.views.*
import dev.entao.log.logd
import dev.entao.theme.ColorX

val Context.popWindow: PopWindow get() = PopWindow(this)

class PopWindow(val context: Context) {
    val popWindow = PopupWindow(context)
    val rootView: LinearLayout


    var onDismissCallback: (PopWindow) -> Unit = {}

    private var xOffset: Int = 0
    private var yOffset: Int = 0
    private var gravity: Int = 0

    init {
        popWindow.apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isFocusable = true
            isOutsideTouchable = true
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 10.dpf
            val d = Shapes.rect {
                corner(6)
                fill(ColorX.dialogBackground)
                stroke(1, ColorX.theme)
            }
            setBackgroundDrawable(d)

            //这个方法在5.0上不回调!!
            setOnDismissListener(onDismissListener)
        }
        rootView = LinearLayout(context).vertical().apply {
            minimumWidth = 150.dp
            padding(5)
            divider()
            gravityLeftCenter()
        }
        popWindow.contentView = rootView
    }

//    override fun  dismi

    private val onDismissListener = PopupWindow.OnDismissListener {
        logd("Dismiss PopWindow")
        onDismissCallback(this)
        onDismissCallback = {}
        logd("Dismiss PopWindow")
    }


    fun position(xOffset: Int, yOffset: Int, gravity: Int = Gravity.NO_GRAVITY): PopWindow {
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.gravity = gravity
        return this
    }


    private fun show(anchorView: View) {
        PopupWindowCompat.showAsDropDown(popWindow, anchorView, xOffset, yOffset, gravity)
    }


    fun showList(anchorView: View, items: Collection<String>, callback: (String) -> Unit) {
        if (items.size > 30) {
            context.toast("列表内容太太多!")
            return
        }
        rootView.apply {
            for (item in items) {
                textView {
                    stylePrimaryText()
                    textColorPrimary()
                    gravityLeftCenter()
                    paddingX(15)
                    paddingY(10)
                    text = item
                    setOnClickListener {
                        popWindow.dismiss()
                        callback(item)
                    }
                }
            }
        }
        show(anchorView)
    }


}