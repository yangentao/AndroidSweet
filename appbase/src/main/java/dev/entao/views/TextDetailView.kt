package dev.entao.views

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import dev.entao.appbase.dp
import dev.entao.theme.Space

class TextDetailView(context: Context) : LinearLayout(context) {
    val textView: TextView
    val detailView: TextView

    init {
        horizontal()
        gravityCenterVertical()
        paddings(Space.X, Space.Y)
        backColorWhiteFade()
        this.minimumHeight = 50.dp
        this.layoutParams = Params.margin.widthFill.heightWrap

        textView = textView {
            linearParams {
                gravityLeftCenter
                widthFlex(1).heightWrap
            }
            stylePrimaryText()
            singleLine()
        }
        detailView = textView {
            linearParams {
                wrap.gravityRightCenter
            }
            styleSecondaryText()
            gravityRightCenter()
            multiLine()
            maxLines(2)
        }
    }

}