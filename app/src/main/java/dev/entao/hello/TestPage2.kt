package dev.entao.hello

import android.widget.LinearLayout
import dev.entao.log.logd
import dev.entao.page.LinearPage
import dev.entao.page.toast
import dev.entao.theme.ColorX
import dev.entao.views.backColor

class TestPage2 : LinearPage() {
    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)
        titleBar {
            title("Test2")
            text("Test") {
//                hello2()
                toast("Hello Yang")
            }
        }


        contentView.apply {
            backColor(ColorX.redLight)
        }

    }



}