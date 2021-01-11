@file:Suppress("MemberVisibilityCanBePrivate", "unused", "ObjectPropertyName")

package dev.entao.page

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import dev.entao.appbase.*
import dev.entao.theme.ColorX

/**
 * Created by yangentao on 16/3/12.
 */

open class BaseActivity : AppCompatActivity(), MsgListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        statusBarColor(ColorX.theme.darkColor)
        App.init(this.application)
        MsgCenter.listenAll(this)
    }


    override fun onMsg(msg: Msg) {

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Perm.onPermResult(requestCode)
    }


    fun statusBarColor(color: Int) {
        val w = window ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.statusBarColor = color
        }
    }


    override fun onDestroy() {
        MsgCenter.remove(this)
        super.onDestroy()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        reqMap.remove(requestCode)?.invoke(ActivityResult(requestCode, resultCode, data))
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun startActivityResult(intent: Intent, block: ActivityResultBlock) {
        val n = genRequestCode()
        reqMap[n] = block
        this.startActivityForResult(intent, n)
    }

    companion object {
        private var reqCode_: Int = 30000
        private val reqMap = HashMap<Int, ActivityResultBlock>()

        @Synchronized
        private fun genRequestCode(): Int {
            reqCode_ += 1
            if (reqCode_ > 31000) {
                reqCode_ = 30000
            }
            return reqCode_
        }


    }

}
typealias ActivityResultBlock = (ActivityResult) -> Unit

class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?) {
    val OK: Boolean get() = Activity.RESULT_OK == resultCode
}

fun <T : BaseActivity> T.canInstallPackage(block: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
        startActivityResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))) {
            block(it.OK && packageManager.canRequestPackageInstalls())
        }
        return
    }
    block(true)
}



