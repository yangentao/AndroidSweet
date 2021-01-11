@file:Suppress("unused", "FunctionName")

package dev.entao.appbase

import android.graphics.Color


//0x80A0B0C0, Color.argb(0x80,0xA0,0xB0,0XC0)
val Long.argb: Int
    get() {
        return (this and 0xFFFFFFFFL).toInt()
    }

val Long.rgb: Int
    get() {
        return ((this and 0x00FFFFFFL) or 0xFF000000).toInt()
    }

val Int.argb: Int
    get() {
        return this.toLong().argb
    }

val Int.rgb: Int
    get() {
        return this.toLong().rgb
    }

val Int.gray get() = grayColor(this)

val String.color: Int
    get() {
        return colorParse(this)
    }

fun grayColor(n: Int): Int {
    return Color.rgb(n, n, n)
}


fun RGB(r: Int, g: Int, b: Int): Int {
    return Color.rgb(r, g, b)
}

fun ARGB(a: Int, r: Int, g: Int, b: Int): Int {
    return Color.argb(a, r, g, b)
}

//#8f8, #800f, #ff884422
fun colorParse(colorString: String): Int {
    if (colorString[0] == '#') {
        val len = colorString.length
        if (len == 4 || len == 5) {
            val sb = StringBuffer(10)
            sb.append('#')
            for (i in 1 until len) {
                sb.append(colorString[i])
                sb.append(colorString[i])
            }
            return Color.parseColor(sb.toString())
        }
    }
    return Color.parseColor(colorString)
}


val Int.darkColor: Int
    get() {
        return darkColor(this, 0.5)
    }

fun Int.multiColor(m: Double): Int {
    return darkColor(this, m)
}

fun darkColor(color: Int, percent: Double): Int {
    val red: Int = Color.red(color)
    val green: Int = Color.green(color)
    val blue: Int = Color.blue(color)
    val alpha: Int = Color.alpha(color)

    return Color.argb(alpha, (red * percent).toInt() % 256, (green * percent).toInt() % 256, (blue * percent).toInt() % 256)
}