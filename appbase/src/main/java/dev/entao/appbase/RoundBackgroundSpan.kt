package dev.entao.appbase

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan

class RoundBackgroundSpan(backColor: Int, textColor: Int, radiusDp: Int) : ReplacementSpan() {
    private var backColor = Color.GRAY
    private var textColor = Color.argb(255, 30, 30, 30)
    private var radius = 4.dp


    init {
        setBackColor(backColor)
        setTextColor(textColor)
        setRadius(radiusDp)
    }

    fun setBackColor(backColor: Int) {
        this.backColor = backColor
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    fun setRadius(dp: Int) {
        this.radius = dp.dp
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int,
        bottom: Int, paint: Paint
    ) {
        val rect = RectF(x, top.toFloat(), x + measureText(paint, text, start, end), bottom.toFloat())
        paint.color = backColor
        canvas.drawRoundRect(rect, radius.toFloat(), radius.toFloat(), paint)
        paint.color = textColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return Math.round(measureText(paint, text, start, end))
    }

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int): Float {
        return paint.measureText(text, start, end)
    }
}