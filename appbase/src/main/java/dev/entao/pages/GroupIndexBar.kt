package dev.entao.pages


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import dev.entao.appbase.*
import dev.entao.log.logd
import dev.entao.theme.ColorX
import dev.entao.views.*


class GroupIndexBar(context: Context) : LinearLayout(context) {
    var labelColor: Int = ColorX.textPrimary
    private var items: List<String> = emptyList()
    private var currentLabel: String? = null
    private var feedbackView: TextView

    var onLabelChanged: (String) -> Unit = {
        logd(it)
    }


    init {
        this.vertical()
        this.clickable()

        feedbackView = TextView(this.context)
        feedbackView.textColor(Color.WHITE).textSize(50).gravityCenter().gone()
        feedbackView.backRectRound(4) {
            fill(0x555555.rgb)
            stroke(2, 0xdddddd.rgb)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val y = event.y.toInt()
        when (action) {
            MotionEvent.ACTION_DOWN -> selectByY(y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> selectByY(y)
            MotionEvent.ACTION_MOVE -> selectByY(y)
        }

        return super.onTouchEvent(event)
    }


    private fun selectByY(y: Int) {
        for (i in 0 until childCount) {
            val itemView = getChildAt(i)
            if (y >= itemView.top && y <= itemView.bottom) {
                val s = itemView.tag as? String
                if (s != null && s != this.currentLabel) {
                    this.currentLabel = s
                    Task.mergeX("label_changed", 50) {
                        fireChanged()
                    }
                }
            }
        }
        updateLabelSelectState()
    }

    private fun fireChanged() {
        val cc = this.currentLabel ?: return
        this.onLabelChanged(cc)
        if (feedbackView.parentGroup == null) {
            val rl = this.parentGroup as? RelativeLayout ?: return
            rl.addView(feedbackView, Params.relative.parentCenter.size(70))
        }
        feedbackView.text = cc
        feedbackView.visiable()
        Task.merge("hide_feedback_view", 650) {
            feedbackView.gone()
        }
    }


    fun setLabelItems(items: List<String>) {
        this.items = items
        this.rebuild()
    }

    private fun rebuild() {
        this.removeAllViews()
        for (s in items) {
            this.textView(Params.linear.widthFill.flexY.gravityCenter) {
                this.tag = s
                this.text(s).textSizeThirdly().gravityCenter()
                this.typeface = Typeface.MONOSPACE
                this.textColorList(labelColor) {
                    selected(Color.WHITE)
                }
                this.backColorList(Color.TRANSPARENT) {
                    selected(Color.GRAY)
                }
            }
        }
        if (this.items.isEmpty()) {
            this.gone()
        } else {
            this.visiable()
        }
    }

    fun setCurrentLabel(label: String) {
        this.currentLabel = label
        Task.mergeX("set_curr_label", 20) {
            updateLabelSelectState()
        }
    }

    private fun updateLabelSelectState() {
        val lb = this.currentLabel ?: "__"
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            v.isSelected = v.tag == lb
        }
    }

    companion object {
        const val WIDTH_PREFER = 30
    }

}
