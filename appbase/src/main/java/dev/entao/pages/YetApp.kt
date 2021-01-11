package dev.entao.pages

import android.app.Application
import dev.entao.json.YsonObject
import dev.entao.appbase.App
import dev.entao.log.logd

/**
 * Created by yet on 2015/10/10.
 */
open class YetApp : Application(), AppVisibleListener {

//	override fun attachBaseContext(base: Context?) {
//		super.attachBaseContext(base)
    //api >= 21, not need
//		MultiDex.install(this)
//	}

    override fun onCreate() {
        super.onCreate()
        App.init(this)
    }

    override fun onEnterForeground() {
    }

    override fun onEnterBackground() {
    }

    open fun onNotifyClick(yo: YsonObject) {
        logd("onNotifyClick:", yo.toString())
    }
}

interface AppVisibleListener {
    fun onEnterForeground()

    fun onEnterBackground()
}
