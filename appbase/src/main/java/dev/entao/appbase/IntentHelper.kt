package dev.entao.appbase

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import dev.entao.json.YsonObject
import dev.entao.base.yson
import dev.entao.log.Yog

/**
 * 明确带有Sys后缀的函数, 是面向Android系统的全局回调/调用
 * 不带Sys后缀的是面向App内的
 *
 *
 * 带有Class参数的, 全部都是针对应用程序内的Class!!!---一般是Activity和Service
 * pendingBroadcast是回调本App组件!!!!----
 * pendingBroadcastSys回调会向Android系统广播
 * broadcast函数只向本App内广播
 * broadcastSys函数向整个Android系统广播
 *
 * @author yangentao@gmail.com
 */


object IntentHelper {

    fun pendingActivity(cls: Class<out Activity>, flag: Int, yo: YsonObject): PendingIntent {
        val it = Intent(App.inst, cls)
        it.yson = yo
        return PendingIntent.getActivity(App.inst, 0, it, flag)
    }

    fun pendingService(cls: Class<out Service>, flag: Int, yo: YsonObject): PendingIntent {
        val it = Intent(App.inst, cls)
        it.yson = yo
        return PendingIntent.getService(App.inst, 0, it, flag)
    }

    fun pendingBroadcastApp(action: String, flag: Int, yo: YsonObject): PendingIntent {
        val it = Intent(action)
        it.yson = yo
        it.setPackage(App.packageName)
        return PendingIntent.getBroadcast(App.inst, 0, it, flag)
    }

    fun pendingBroadcastSys(action: String, flag: Int, yo: YsonObject): PendingIntent {
        val it = Intent(action)
        it.yson = yo
        return PendingIntent.getBroadcast(App.inst, 0, it, flag)
    }

    fun pendingBroadcast(cls: Class<out BroadcastReceiver>, yo: YsonObject): PendingIntent {
        val it = Intent(App.inst, cls)
        it.yson = yo
        return PendingIntent.getBroadcast(App.inst, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun startService(cls: Class<out Service>, action: String, yo: YsonObject) {
        startService(cls, action, null, yo)
    }

    fun startService(cls: Class<out Service>, uri: Uri) {
        startService(cls, null, uri, YsonObject())
    }

    fun startService(cls: Class<out Service>, action: String?, uri: Uri?, yo: YsonObject) {
        val it = Intent(action, uri, App.inst, cls)
        it.yson = yo
        App.inst.startService(it)
    }

    fun startService(cls: Class<out Service>, yo: YsonObject) {
        val it = Intent(App.inst, cls)
        it.yson = yo
        App.inst.startService(it)
    }


    fun broadcast(action: String, yo: YsonObject) {
        broadcast(action, null, yo)
    }

    fun broadcast(action: String, uri: Uri?, yo: YsonObject) {
        val it = Intent(action, uri)
        it.yson = yo
        it.setPackage(App.packageName)
        App.inst.sendBroadcast(it)
    }


    fun broadcastSys(action: String, uri: Uri?, yo: YsonObject) {
        val it = Intent(action, uri)
        it.yson = yo
        App.inst.sendBroadcast(it)
    }


    fun dump(it: Intent?) {
        Yog.d("Dump Intent")
        if (it == null) {
            Yog.d("NULL")
            return
        }
        Yog.d("ACTION:", it.action)
        dump(it.extras)
        Yog.d("Dump Intent  END")
    }

    fun dump(b: Bundle?) {
        dump("", b)
    }

    private fun dump(prefix: String, b: Bundle?) {
        if (b != null) {
            for (key in b.keySet()) {
                val value = b.get(key)
                if (value is Bundle) {
                    Yog.d(prefix, key)
                    dump("$prefix        ", value)
                } else {
                    Yog.d(prefix, key, value, if (value == null) " null " else value.javaClass.simpleName)
                }

            }
        }
    }
}


