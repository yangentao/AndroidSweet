@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.entao.appbase.*
import dev.entao.page.Page
import dev.entao.pages.act
import dev.entao.base.BlockUnit
import dev.entao.base.notEmptyTrimed
import dev.entao.log.logd
import dev.entao.theme.ColorX
import dev.entao.theme.Dimen
import dev.entao.theme.heightButton

class XDialogButton(val text: String, val color: Int, val callback: (String) -> Unit) {
    var backColor: Int = ColorX.dialogBackground
}

class XDialogButtonList {
    val buttons = ArrayList<XDialogButton>()

    operator fun plus(btn: XDialogButton) {
        buttons += btn
    }

    fun button(text: String, color: Int, block: (String) -> Unit): XDialogButton {
        val b = XDialogButton(text, color, block)
        buttons.add(b)
        return b
    }

    fun risk(text: String, block: (String) -> Unit): XDialogButton {
        return button(text, ColorX.risk, block)
    }

    fun safe(text: String, block: (String) -> Unit): XDialogButton {
        return button(text, ColorX.safe, block)
    }

    fun accent(text: String, block: (String) -> Unit): XDialogButton {
        return button(text, ColorX.accent, block)
    }

    fun normal(text: String, block: (String) -> Unit): XDialogButton {
        return button(text, ColorX.textPrimary, block)
    }

    fun cancel(text: String = "取消"): XDialogButton {
        return normal(text) {}
    }

    fun ok(text: String = "确定", block: (String) -> Unit): XDialogButton {
        return accent(text, block)
    }

}

class XDialogBody(val context: Context) {
    lateinit var bodyView: View
    var bodyViewParam: LinearLayout.LayoutParams = Params.linear.widthFill.heightWrap

    fun text(text: String, block: TextView.() -> Unit = {}) {
        val tv = TextView(context).apply {
            textSizeSecondary()
            textColorPrimary()
            padding(15, 15, 15, 15)
            linkifyAll()
            this.textS = text
            logd("dialog text: ", this.textS)
            if (text.length < 24) {
                gravityCenter()
            } else {
                gravityLeftCenter()
                multiLine()
            }
        }
        tv.block()
        bodyView = tv
    }

    fun input(block: EditText.() -> Unit): EditText {
        val rl = RelativeLayout(context).needId()
        val ed = rl.editText(Params.relative.parentCenter.widthFill.heightWrap.margins(15)) {
            minimumWidth = 200.dp
            minimumHeight = Dimen.editHeight.dp
            withClearButton()
            this.block()
        }
        bodyView = rl
        return ed
    }

    fun inputLines(block: EditText.() -> Unit): EditText {
        val rl = RelativeLayout(context).needId()
        val ed = rl.editText(Params.relative.parentCenter.widthFill.heightWrap.margins(15)) {
            minimumWidth = 200.dp
            minimumHeight = (Dimen.editHeight * 5).dp
            this.multiLine()
            minLines = 5
            gravityTopLeft()
            padding(5)
            this.block()
        }
        bodyView = rl
        return ed
    }

    fun list(block: ListViewX.() -> Unit): ListViewX {
        bodyViewParam = Params.linear.widthFill.flexY
        val lv = ListViewX(context)
        bodyView = lv
        lv.block()
        return lv
    }


    fun listCheck(block: ListViewCheckable.() -> Unit): ListViewCheckable {
        bodyViewParam = Params.linear.widthFill.flexY
        return ListViewCheckable(context).apply {
            bodyView = this
            this.block()
        }
    }


    fun grid(block: GridViewX.() -> Unit): GridViewX {
        bodyViewParam = Params.linear.widthFill.flexY
        return GridViewX(context).apply {
            bodyView = this
            this.block()
        }
    }

    fun gridCheck(block: GridViewCheck.() -> Unit): GridViewCheck {
        bodyViewParam = Params.linear.widthFill.flexY
        return GridViewCheck(context).apply {
            bodyView = this
            this.block()
        }
    }

}

class XDialog(val context: Context) {
    val dialog = Dialog(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog)

    var titleHeight = 45

    val contentLayout = LinearLayout(context).vertical()
    val contentParams: GroupParams = Params.group.wrap

    var bodyView: View = TextView(context).styleSecondaryText().textColorPrimary()
    var bodyViewParam: LinearParams = Params.linear.widthFill.heightWrap

    var titleView: View? = null
    val titleParams: LinearParams = Params.linear.widthFill.height(titleHeight)

    var onDismiss: (XDialog) -> Unit = {}


    private val buttonList = XDialogButtonList()

    var closeAfterSeconds = 0

    var argS: String = ""
    var argN = 0


    init {
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setDimAmount(0.5f)
            }
        }
        windowParams {
            wrap
//            horizontalMargin = 0.2f
//            verticalMargin = 0.2f
            gravity = Gravity.CENTER
        }
        contentLayout.apply {
            backRectRound(Dimen.dialogCorner) {
                fill(ColorX.dialogBackground)
            }
            divider()
            clipToOutline = true
            minimumWidth = 160.dp
            minimumHeight = 80.dp
        }

        dialog.setOnDismissListener(::onDismissCallback)
    }

    private fun onDismissCallback(d: DialogInterface) {
        onDismiss(this)
        onDismiss = {}
    }

    fun windowParams(block: WindowManager.LayoutParams.() -> Unit) {
        val windowParam: WindowManager.LayoutParams = dialog.window?.attributes!!
        windowParam.block()
        dialog.window?.attributes = windowParam
    }

    fun title(title: String?, block: TextView.() -> Unit) {
        if (title == null || title.isEmpty()) {
            titleView = null
            return
        }
        val tv = TextView(context).apply {
            textColorTitle()
            textSizeTitle()
            setBackgroundColor(ColorX.dialogTitleBar)
            gravityCenter()
            textS = title
        }
        tv.block()
        title(tv)
    }

    fun title(title: String?) {
        title(title) {}
    }

    fun title(view: View) {
        this.titleView = view
    }

    fun buttons(block: XDialogButtonList.() -> Unit): XDialog {
        buttonList.block()
        return this
    }


    fun body(view: View, params: LinearLayout.LayoutParams = Params.linear.widthFill.heightWrap) {
        this.bodyView = view
        this.bodyViewParam = params
    }

    fun body(block: XDialogBody.() -> Unit) {
        val db = XDialogBody(context)
        db.block()
        body(db.bodyView, db.bodyViewParam)
    }

    fun bodyView(block: () -> View) {
        body(block())
    }

    fun show() {
        dialog.setContentView(contentLayout, Params.group.wrap)
        titleView?.also {
            contentLayout.addView(it, titleParams)
        }
        bodyView.minimumHeight = 60.dp
        contentLayout.addView(bodyView, bodyViewParam)
        if (buttonList.buttons.isNotEmpty()) {
            contentLayout.linearLayoutH(Params.linear.widthFill.heightButton) {
                divider()
                for (b in buttonList.buttons) {
                    textView(Params.linear.gravityCenter.heightFill.flexX) {
                        textSizeTitle()
                        textColor(b.color)
                        backColor(b.backColor)
                        padding(15, 10, 15, 10)
                        gravityCenter()
                        textS = b.text
                        click {
                            dismiss()
                            b.callback(b.text)
                        }
                    }
                }
            }
        }
        dialog.show()
        val tm = closeAfterSeconds
        if (tm > 1) {
            Task.foreDelay(tm * 1000L) {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }
    }


    fun dismiss() {
        dialog.dismiss()
    }


}


fun XDialog.showAlert(msg: String) {
    showAlert(null, msg)
}

fun XDialog.showAlert(title: String?, msg: String, dismissCallback: () -> Unit = {}) {
    title(title)
    body {
        text(msg) {}
    }
    buttons {
        ok { }
    }
    onDismiss = {
        dismissCallback()
    }
    show()
}


fun XDialog.showConfirm(title: String?, msg: String, onOK: () -> Unit) {
    title(title)
    body {
        text(msg) {}
    }
    buttons {
        cancel()
        ok { onOK() }
    }
    show()
}


fun XDialog.showInput(title: String?, value: String = "", onOK: (String) -> Unit) {
    this.showInput(title, value, {}, onOK)
}

fun XDialog.showInput(title: String?, value: String, configBlock: (EditText) -> Unit, onOK: (String) -> Unit) {
    var ed: EditText? = null
    title(title)
    body {
        ed = input {
            this.textS = value
            configBlock(this)
        }
    }
    buttons {
        cancel()
        ok {
            val s = ed?.textS ?: ""
            onOK(s)
        }
    }
    show()
}


fun XDialog.showInputLines(title: String?, value: String = "", onOK: (String) -> Unit) {
    var ed: EditText? = null
    title(title)
    body {
        ed = inputLines {
            this.textS = value
        }
    }
    buttons {
        cancel()
        ok {
            onOK(ed?.textS ?: "")
        }
    }
    show()
}

fun XDialog.showInputInteger(title: String, n: Long?, block: (Long) -> Unit) {
    this.showInput(title, n?.toString() ?: "", {
        it.inputTypeNumber()
    }) { s ->
        s.notEmptyTrimed {
            it.toLongOrNull()?.also { lv ->
                block(lv)
            }
        }

    }
}

fun XDialog.showInputDouble(title: String, n: Double?, block: (Double) -> Unit) {
    this.showInput(title, n?.toString() ?: "", {
        it.inputTypeNumberDecimal()
    }) { s ->
        s.notEmptyTrimed {
            it.toDoubleOrNull()?.also { lf ->
                block(lf)
            }
        }
    }
}

fun XDialog.showListX(title: String?, items: List<Any>, block: ListViewX.() -> Unit, onResult: (Int, Any) -> Unit) {
    title(title)
    body {
        list {
            setItems(items)
            this.clickCallback = object : ListClickCallback {
                override fun onClickAdapter(position: Int, item: Any) {
                    dismiss()
                    onResult(position, item)
                }
            }
            this.block()
        }
    }
    buttons {
        cancel()
    }
    show()
}


@Suppress("UNCHECKED_CAST")
fun <T : Any> XDialog.showListItems(title: String?, items: List<T>, textBlock: (T) -> String, onResult: (Int, T) -> Unit) {
    showListX(title, items, {
        this.callback = object : ListCallback {
            override fun onBindView(itemView: View, item: Any) {
                itemView as TextView
                itemView.text = textBlock(item as T)
            }
        }
    }) { p, item ->
        onResult(p, item as T)
    }
}


fun XDialog.showListString(title: String?, items: List<String>, onResult: (Int, String) -> Unit) {
    showListX(title, items, {}) { p, item ->
        onResult(p, item as String)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> XDialog.showListDetail(title: String?, items: List<T>, textBlock: (T) -> Pair<String, String>, onResult: (Int, T) -> Unit) {
    showListX(title, items, {
        this.callback = object : ListCallback {
            override fun onNewView(context: Context, position: Int): View {
                return TextDetailView(context)
            }

            override fun onBindView(itemView: View, item: Any) {
                itemView as TextDetailView
                val p = textBlock(item as T)
                itemView.textView.text = p.first
                itemView.detailView.text = p.second
            }
        }
    }) { p, item ->
        onResult(p, item as T)
    }

}

fun XDialog.showListCheck(title: String?, items: List<Any>, block: ListViewCheckable.() -> Unit, onResult: (List<Any>) -> Unit) {
    var lv: ListViewCheckable? = null
    title(title)
    body {
        lv = listCheck {
            setItems(items)
            this.block()
        }
    }
    buttons {
        cancel()
        ok {
            onResult(lv!!.checkedItems)
        }
    }
    show()
}

fun XDialog.showListCheck(title: String?, items: List<Any>, checkedItems: List<Any>, textBlock: (Any) -> String, onResult: (List<Any>) -> Unit) {

    showListCheck(title, items, {
        check(true, checkedItems)
        checkCallback = object : ItemCheckCallback {
            override fun onItemKey(item: Any): String {
                return textBlock(item)
            }
        }
        callback = object : ListCallback {
            override fun onBindView(itemView: View, item: Any) {
                itemView as TextView
                itemView.text = textBlock(item)
            }
        }
    }, onResult)


}


fun XDialog.showListCheckString(title: String?, items: List<String>, checkedItems: List<String>, onResult: (List<String>) -> Unit) {
    this.showListCheckItem(title, items, checkedItems, { it }, onResult)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> XDialog.showListCheckItem(title: String?, items: List<T>, checkedItems: List<T>, textBlock: (T) -> String, onResult: (List<T>) -> Unit) {
    showListCheck(title, items, checkedItems, { textBlock(it as T) }) {
        onResult(it as List<T>)
    }
}


fun XDialog.showGrid(title: String?, items: List<Any>, block: GridViewX.() -> Unit, onResult: (Int, Any) -> Unit) {
    title(title)
    body {
        grid {
            enableLine = true
            setItems(items)
            this.clickCallback = object : AdapterClickCallback {
                override fun onClickAdapter(position: Int, item: Any) {
                    dismiss()
                    onResult(position, item)
                }
            }
            this.block()
        }
    }
    buttons {
        cancel()
    }
    show()
}

fun XDialog.showGridCheck(title: String?, items: List<Any>, block: GridViewCheck.() -> Unit, onResult: (List<Any>) -> Unit) {
    var gv: GridViewCheck? = null
    title(title)
    body {
        gv = gridCheck {
            enableLine = true
            setItems(items)
            this.block()
        }
    }
    buttons {
        cancel()
        ok {
            onResult(gv!!.checkedItems)
        }
    }
    show()
}


@Suppress("UNCHECKED_CAST")
fun XDialog.showGridMenu(title: String?, items: List<Pair<String, Int>>, columnCount: Int, imgSize: Int, imgColor: Int, onResult: (String) -> Unit) {
    title(title)
    body {
        grid {
            enableLine = true
            numColumns = columnCount
            setItems(items)
            this.callback = object : GridCallback {
                override fun onBindView(itemView: View, item: Any) {
                    itemView as TextView
                    item as Pair<String, Int>
                    itemView.text = item.first
                    itemView.topImage = item.second.resDrawable.mutate().tinted(imgColor).sized(imgSize)
                }
            }
            this.clickCallback = object : AdapterClickCallback {
                override fun onClickAdapter(position: Int, item: Any) {
                    item as Pair<String, Int>
                    dismiss()
                    onResult(item.first)
                }
            }
        }
    }
    bodyViewParam = Params.linear.wrap
    show()
}


val Fragment.xdialog: XDialog
    get() {
        return XDialog(this.act)
    }
val Activity.xdialog: XDialog
    get() {
        return XDialog(this)
    }

val Page.xdialog get() = XDialog(this.context)


fun Fragment.alert(msg: String) {
    this.xdialog.showAlert(msg)
}

fun Fragment.alert(msg: String, block: BlockUnit) {
    this.xdialog.showAlert(null, msg, block)
}

fun Fragment.confirm(msg: String, block: BlockUnit) {
    this.xdialog.showConfirm(null, msg, block)
}


fun Activity.alert(msg: String) {
    this.xdialog.showAlert(msg)
}

fun Activity.alert(msg: String, block: BlockUnit) {
    this.xdialog.showAlert(null, msg, block)
}

fun Activity.confirm(msg: String, block: BlockUnit) {
    this.xdialog.showConfirm(null, msg, block)
}


