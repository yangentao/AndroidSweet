package dev.entao.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import dev.entao.theme.ColorX
import dev.entao.appbase.ShapeRect
import dev.entao.appbase.Task

/**
 * Created by yet on 2015/10/31.
 */
class TopProgressBar(context: Context) : View(context) {
    var duration = 200//动画时间 200毫秒---从0到最大值(100%)的时间
    var foreDrawable: Drawable = ShapeRect(ColorX.Progress).value
    private var max = 100
    private var progress = 0
    private var animProgress = 0

    private var hiding = false

    val isVisible: Boolean
        get() = visibility == VISIBLE


    init {
        backColorWhite()
    }

    override fun onDraw(canvas: Canvas) {
        var prog = progress
        if (animProgress >= 0) {
            prog = animProgress
        }
        val width = width
        max = if (max == 0) 1 else max
        var w = prog * width / max
        w = if (w < 0) -w else w
        foreDrawable.setBounds(0, 0, w, height)
        foreDrawable.draw(canvas)

    }

    private fun runMe() {//每次递进1%, 或1
        val times = duration / INTERVAL_A
        if (times < 1) {
            animProgress = progress
            postInvalidate()
            return
        }
        var percent = max / times
        if (percent == 0) {
            percent = 2
        }
        val cha = progress - animProgress
        if (Math.abs(cha) <= percent) {
            animProgress = progress
            postInvalidate()
            return
        }
        if (cha > 0) {
            animProgress += percent
        } else {
            animProgress -= percent
        }
        postInvalidate()
        Task.foreDelay(INTERVAL_A.toLong()) {
            runMe()
        }


    }


    fun setForeDrawable(drawable: Drawable): TopProgressBar {
        foreDrawable = drawable
        postInvalidate()
        return this
    }

    fun setProgress(progress: Int) {
        Task.mainThread {
            var p = progress
            if (p < 0) {
                p = 0
            }
            if (p > max) {
                p = max
            }
            this.animProgress = this.progress
            this.progress = p
            Task.fore {
                runMe()
            }
        }
    }

    fun setMax(max: Int) {
        Task.mainThread {
            var x = max
            if (x < 0) {
                x = 1
            }
            this.max = x
            postInvalidate()
        }
    }

    fun show(): TopProgressBar {
        visibility = VISIBLE
        hiding = false
        return this
    }

    fun show(max: Int) {
        Task.mainThread {
            visibility = VISIBLE
            this.progress = 0
            this.animProgress = 0
            setMax(max)
        }
    }


    fun hide() {
        hiding = true
        Task.mainThread {
            if (hiding) {
                visibility = GONE
            }
        }
    }

    fun postHide(delay: Long) {
        hiding = true
        Task.foreDelay(delay) {
            hide()
        }
    }

    companion object {
        private const val INTERVAL_A = 20//动画, 20毫秒刷新一次 , 有限

        fun makeDrawable(cornerRadius: Int, color: Int): GradientDrawable {
            val gd = GradientDrawable()
            gd.cornerRadius = cornerRadius.toFloat()
            gd.setColor(color)
            return gd
        }


    }
}
