@file:Suppress("unused")

package dev.entao.ui.util.app

import android.os.Build
import android.os.Build.VERSION
import dev.entao.base.lowerCased
import java.util.*

object OS {

    val HUAWEI = isManufacturer("HUAWEI")


    fun incremental(): String {
        return VERSION.INCREMENTAL
    }

    fun isManufacturer(s: String): Boolean {
        return hasNoCase(Build.MANUFACTURER, s)
    }

    fun isManufacturerEq(s: String): Boolean {
        return Build.MANUFACTURER?.lowerCased == s.lowerCased
    }

    fun isModel(s: String): Boolean {
        return hasNoCase(Build.MODEL, s)
    }

    fun isModels(vararg ms: String): Boolean {
        for (m in ms) {
            if (isModel(m)) {
                return true
            }
        }
        return false
    }

    /**
     * model字符串包含m, 并且API >= api

     * @param m
     * *
     * @param api
     * *
     * @return
     */
    fun isModelApi(m: String, api: Int): Boolean {
        return isModel(m) && VERSION.SDK_INT >= api
    }

    fun isHardware(s: String): Boolean {
        return hasNoCase(Build.HARDWARE, s)
    }

    fun model(): String {
        return Build.MODEL
    }

    fun manufacturer(): String {
        return Build.MANUFACTURER
    }

    fun hardware(): String {
        return Build.HARDWARE
    }

    /**
     * s1字符串是否包含s2, 都是null会返回true, 不区分大小写

     * @param s1
     * *
     * @param s2
     * *
     * @return
     */
    private fun hasNoCase(s1: String?, s2: String?): Boolean {
        if (s1 != null && s2 != null) {
            return s1.toLowerCase(Locale.getDefault()).contains(s2.toLowerCase(Locale.getDefault()))
        }
        return s1 === s2
    }

    /**
     * s1字符串是否包含s2, 都是null会返回true

     * @param s1
     * *
     * @param s2
     * *
     * @return
     */
    private fun has(s1: String?, s2: String?): Boolean {
        if (s1 != null && s2 != null) {
            return s1.indexOf(s2) >= 0
        }
        return s1 === s2
    }

}
