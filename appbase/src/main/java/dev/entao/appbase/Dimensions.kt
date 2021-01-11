package dev.entao.appbase


val Int.dp: Int get() = (this * App.density).toInt()
val Int.dpf: Float get() = this * App.density
val Float.dpf: Float get() = this * App.density
val Double.dpf: Float get() = (this * App.density).toFloat()

val Int.sp: Int get() = (this * App.scaledDensity).toInt()
val Int.spf: Float get() = this * App.scaledDensity
val Float.spf: Float get() = this * App.scaledDensity
val Double.spf: Float get() = (this * App.scaledDensity).toFloat()


val Int.toDP: Int get() = (this / App.density).toInt()
val Float.toDP: Float get() = this / App.density
val Float.px2dp: Float get() = this / App.density

val Int.toSP: Int get() = (this / App.scaledDensity).toInt()
val Float.toSP: Float get() = this / App.scaledDensity


fun dp(n: Int): Int {
    return n.dp
}


fun px2dp(px: Int): Int {
    return px.toDP
}