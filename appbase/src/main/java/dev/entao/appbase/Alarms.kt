@file:Suppress("unused")

package dev.entao.appbase

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

object Alarms {

    fun serviceAt(time: Long, wakeup: Boolean, action: String, cls: Class<out Service>, uri: Uri? = null, bundle: Bundle? = null) {
        val it = Intent(action, uri, App.inst, cls)
        if (bundle != null) {
            it.putExtras(bundle)
        }
        val pi = PendingIntent.getService(App.inst, 0, it, PendingIntent.FLAG_ONE_SHOT)
        val am = App.inst.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(if (wakeup) AlarmManager.RTC_WAKEUP else AlarmManager.RTC, time, pi)
    }

    fun broadcastAt(time: Long, wakeup: Boolean, action: String, cls: Class<out BroadcastReceiver>, uri: Uri? = null, bundle: Bundle? = null) {
        val it = Intent(action, uri, App.inst, cls)
        if (bundle != null) {
            it.putExtras(bundle)
        }
        val pi = PendingIntent.getBroadcast(App.inst, 0, it, PendingIntent.FLAG_ONE_SHOT)
        val am = App.inst.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(if (wakeup) AlarmManager.RTC_WAKEUP else AlarmManager.RTC, time, pi)
    }
}
