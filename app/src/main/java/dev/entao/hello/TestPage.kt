package dev.entao.hello

import android.content.Context
import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.snackbar.Snackbar
import dev.entao.appbase.*
import dev.entao.page.LinearPage
import dev.entao.views.*
import dev.entao.log.logd
import dev.entao.theme.ColorX


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
            text("前进") {
            }
            text("后退") {

            }
            text("Dialog") {
                snackShow("Hello")
            }
        }


        contentView.apply {
            gridViewCheck {
                linearParams {
                    fill
                }
                val lv = this
                setItems(items)
                callback = object : GridCallback {
                    override fun onNewView(context: Context, position: Int): View {
                        val v = super.onNewView(context, position) as AppCompatTextView
                        v.topImage = R.mipmap.reg_ent.resDrawable.sized(40)
                            .tintedList(Color.LTGRAY, ColorX.redLight)
                        v.imagePadding = 0
                        v.layoutParams = Params.list.widthFill.heightWrap
                        return v
                    }
                }
                checkCallback = object : ItemCheckCallback {
                    override fun onCheckChanged(item: Any) {
                        logd(lv.checkedItems)
                    }

                    override fun onItemCheckable(item: Any): Boolean {
                        return item != "A"
                    }
                }

            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        return super.onKeyDown(keyCode, event)
    }
}