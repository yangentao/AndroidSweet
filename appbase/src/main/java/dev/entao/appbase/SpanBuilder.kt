@file:Suppress("unused")

package dev.entao.appbase

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.*

fun spanBuilder(block: SpanBuilder.() -> Unit): SpannableString {
    val b = SpanBuilder()
    b.block()
    return b.value
}

class SpanBuilder {
    class SpanItem(val what: Any, val start: Int, val end: Int, val flags: Int)
    class ItemBuilder {
        var color: Int? = null
        var backColor: Int? = null
        var scale: Float? = null
        var sizeSp: Int? = null
        var family: String? = null
        var fontStyle: Int? = null //Typeface.BOLD
        var strike: Boolean = false
        var underline: Boolean = false
        var subscript: Boolean = false
        var superscript: Boolean = false
        var url: String? = null
        var image: Drawable? = null
        var imageBaseline: Boolean = false


        fun bold() {
            fontStyle = Typeface.BOLD
        }

        fun italic() {
            fontStyle = Typeface.ITALIC
        }

        fun boldItalic() {
            fontStyle = Typeface.BOLD_ITALIC
        }
    }


    private val sb: StringBuilder = StringBuilder(128)
    private val items = ArrayList<SpanItem>(8)

    val value: SpannableString
        get() {
            val ss = SpannableString(sb.toString())
            for (item in items) {
                ss.setSpan(item.what, item.start, item.end, item.flags)
            }
            return ss
        }

    fun color(color: Int, s: String): SpanBuilder {
        return add(s) {
            this.color = color
        }
    }


    fun line(s: String): SpanBuilder {
        sb.append(s).append("\n")
        return this
    }

    fun line(): SpanBuilder {
        sb.append("\n")
        return this
    }

    fun add(s: String): SpanBuilder {
        sb.append(s)
        return this
    }

    fun line(s: String, block: ItemBuilder.() -> Unit): SpanBuilder {
        return add(s, block).line()
    }

    fun line(normal: String, attred: String, block: ItemBuilder.() -> Unit): SpanBuilder {
        return add(normal).add(attred, block).line()
    }

    fun add(s: String, block: ItemBuilder.() -> Unit): SpanBuilder {
        if (s.isEmpty()) {
            return this
        }
        val start = sb.length
        val end = start + s.length
        sb.append(s)
        val b = ItemBuilder()
        b.block()
        if (b.color != null) {
            items += SpanItem(ForegroundColorSpan(b.color!!), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.backColor != null) {
            items += SpanItem(BackgroundColorSpan(b.backColor!!), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.scale != null) {
            items += SpanItem(ScaleXSpan(b.scale!!), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.sizeSp != null) {
            items += SpanItem(AbsoluteSizeSpan(b.sizeSp!!), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.family != null) {
            items += SpanItem(TypefaceSpan(b.family!!), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.fontStyle != null) {
            items += SpanItem(StyleSpan(b.fontStyle!!), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.strike) {
            items += SpanItem(StrikethroughSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.underline) {
            items += SpanItem(UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.subscript) {
            items += SpanItem(SubscriptSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.superscript) {
            items += SpanItem(SuperscriptSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.url != null) {
            items += SpanItem(URLSpan(b.url!!), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (b.image != null) {
            val a = if (b.imageBaseline) {
                ImageSpan(b.image!!, ImageSpan.ALIGN_BASELINE)
            } else {
                ImageSpan(b.image!!)
            }
            items += SpanItem(a, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        return this
    }


    fun backRound(backColor: Int, textColor: Int, radius: Int, s: String): SpanBuilder {
        if (s.isEmpty()) {
            return this
        }
        val start = sb.length
        val end = start + s.length
        sb.append(s)
        items += SpanItem(RoundBackgroundSpan(backColor, textColor, radius), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return this
    }
}