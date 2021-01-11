package dev.entao.hello

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import dev.entao.appbase.dpf
import dev.entao.appbase.fill
import dev.entao.views.*
import dev.entao.theme.ColorX

class ImageTextView(context: Context) : ConstraintLayout(context) {
    val imageView: ImageView
    val textView: TextView

    init {
        backRectRoundFade(20, true) {
            fill(Color.WHITE)
        }
        imageView = imageView {
            constraintParams {
                edgesParent(0.5f, 0.3f)
                widthPercent(0.5f)
                ratioW(1, 1)
            }
            scaleCenterInside()
        }
        textView = textView {
            constraintParams {
                topToBottom = imageView.requireId()
                bottomToBottom = 0
                wrap.edgesParentHor()
            }
            textSize = 20f
            textColor(ColorX.blue)
        }
        this.elevation = 10.dpf
    }
}

class GoodsItemView(context: Context) : ConstraintLayout(context) {
    val imageView: ImageView
    val textView: TextView

    init {

        imageView = imageView {
            constraintParams {
                edgesParent(0.5f, 0.2f)
                widthPercent(0.7f)
                ratioW(1, 1)
            }
            scaleCenterInside()
            padding(20)

            backRectRoundFade(10, false) {
                fill(Color.WHITE)
            }

            this.elevation = 10.dpf
        }
        textView = textView {
            constraintParams {
                topToBottom = imageView.requireId()
                bottomToBottom = 0
                wrap.edgesParentHor()
            }
            textSizeSecondary()
            textColorPrimary()
        }

    }
}