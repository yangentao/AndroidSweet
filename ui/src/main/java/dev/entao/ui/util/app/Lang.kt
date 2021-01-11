package dev.entao.ui.util.app

import java.util.*

/**
 * Created by entaoyang@163.com on 2016-10-16.
 */

object Lang {

    val zh: Boolean
        get() {
            val lo = Locale.getDefault()
            return "zh" == lo.language
        }

    //简体
    val zhCN: Boolean
        get() {
            val lo = Locale.getDefault()
            return "zh" == lo.language && "CN" == lo.country
        }

    //繁体
    val zhOther: Boolean
        get() {
            val lo = Locale.getDefault()
            return "zh" == lo.language && "CN" != lo.country
        }

    //英语
    val en: Boolean
        get() {
            val lo = Locale.getDefault()
            return "en" == lo.language
        }

}