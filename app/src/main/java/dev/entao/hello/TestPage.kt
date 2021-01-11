package dev.entao.hello

import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import dev.entao.appbase.*
import dev.entao.page.LinearPage
import dev.entao.views.*
import dev.entao.log.logd
import dev.entao.theme.ColorX
import dev.entao.appbase.TimeDown


fun Snackbar.config() {
    this.view.backColor(ColorX.theme)
    val p = this.view.layoutParams as ViewGroup.MarginLayoutParams
    p.marginBottom(52)
}


fun TestPage.snackShow(text: String) {
    val n = Snackbar.make(this.contentView, text, Snackbar.LENGTH_LONG)
    n.config()
    n.show()
}

fun TestPage.snackShow(text: String, actionText: String, block: () -> Unit) {
    val n = Snackbar.make(this.contentView, text, Snackbar.LENGTH_INDEFINITE)
    n.config()
    n.setAction(actionText) {
        block()
    }
    n.show()
}

class TestPage : LinearPage() {
    var items = listOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W"
    )
    var items2 = listOf("A", "B", "C", "D", "E")
    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)
        titleBar {
            title("Hello")
            text("开始") {
                TimeDown.start("edPhone", 10)
            }
            text("取消") {
                TimeDown.cancel("edPhone")
            }
        }


        contentView.apply {

        }

    }

    override fun onMsg(msg: Msg) {
        if (msg.isMsg(TimeDown.MSG_TIME_DOWN)) {
            logd(msg.s1, msg.n1)
        }
        super.onMsg(msg)
    }
}