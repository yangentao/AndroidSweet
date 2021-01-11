package dev.entao.ui.util.app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dev.entao.appbase.Msg
import dev.entao.appbase.MsgCenter
import dev.entao.appbase.MsgListener

/**
 * Created by entaoyang@163.com on 2017-03-17.
 */

open class BaseService : Service(), MsgListener {

	override fun onCreate() {
		super.onCreate()
		MsgCenter.listenAll(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		MsgCenter.remove(this)
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	override fun onMsg(msg: Msg) {

	}
}
