package dev.entao.appbase

/**
 * Created by entaoyang@163.com on 16/7/20.
 */
class MySize(var width: Int = 0, var height: Int = 0) {
    fun area(): Int {
        return width * height
    }

    fun maxEdge(): Int {
        return Math.max(width, height)
    }
}
