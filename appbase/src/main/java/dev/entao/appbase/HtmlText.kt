package dev.entao.appbase

import android.os.Build
import android.text.Html
import android.text.Spanned
import dev.entao.theme.ColorX

/**
 * Created by entaoyang@163.com on 2016-11-04.
 */

//textview , font  不支持size属性, 使用big或small代替, big或small可以嵌套
class HtmlText(capcity: Int = 64) {
    var sb = StringBuilder(capcity)


    //含有子元素的标签
    fun tag(name: String, vararg attrs: Pair<String, String>, block: HtmlText.() -> Unit): HtmlText {
        sb.append("<$name")
        for (p in attrs) {
            sb.append(""" ${p.first}="${p.second}"""")
        }
        sb.append(">")
        this.block()
        sb.append("</$name>")
        return this
    }

    //直接关闭的标签, br--><br/>,  <hr/>
    fun tag(name: String, vararg attrs: Pair<String, String>): HtmlText {
        sb.append("<$name")
        for (p in attrs) {
            sb.append(""" ${p.first}="${p.second}" """)
        }
        sb.append("/>")
        return this
    }

    fun text(s: String): HtmlText {
        sb.append(s)
        return this
    }

    //	rgb(x,x,x)
    //	#xxxxxx
    //	colorname   red, green...
    fun font(color: String, block: HtmlText.() -> Unit): HtmlText {
        return tag("font", "color" to color, block = block)
    }

    fun font(color: Int, block: HtmlText.() -> Unit): HtmlText {
        val s = ColorX.toStringColor(color)
        return tag("font", "color" to s, block = block)
    }


    fun br(): HtmlText {
        return tag("br")
    }

    fun big(block: HtmlText.() -> Unit): HtmlText {
        return tag("big", block = block)
    }

    fun small(block: HtmlText.() -> Unit): HtmlText {
        return tag("small", block = block)
    }

    fun strong(block: HtmlText.() -> Unit): HtmlText {
        return tag("strong", block = block)
    }

    fun strike(block: HtmlText.() -> Unit): HtmlText {
        return tag("strike", block = block)
    }

    fun em(block: HtmlText.() -> Unit): HtmlText {
        return tag("em", block = block)
    }
    @Suppress("DEPRECATION")
    fun spanned(): Spanned {
        return if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(sb.toString())
        }
    }
}