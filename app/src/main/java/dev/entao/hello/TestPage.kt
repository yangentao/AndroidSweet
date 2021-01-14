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
import dev.entao.page.toast
import java.io.FileReader


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
    var items = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W")
    var items2 = listOf("A", "B", "C", "D", "E")
    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)
        titleBar {
            title("Test")
            text("开始") {
//                hello2()
                toast("Hello Yang")
            }
            text("Hello") {
                pushPage(TestPage2())
            }
        }


        contentView.apply {
            backColor(ColorX.cyanDark)
        }

    }

    private fun hello() {
        val fr = FileReader("/proc/tty/drivers")
        logd("\n", fr.readText())
        fr.close()
    }

    private fun hello2() {
        val fr = FileReader("/proc/driver/camera_info")
        logd("\n", fr.readText())
        fr.close()
    }

    override fun onMsg(msg: Msg) {
        if (msg.isMsg(TimeDown.MSG_TIME_DOWN)) {
            logd(msg.s1, msg.n1)
        }
        super.onMsg(msg)
    }
}