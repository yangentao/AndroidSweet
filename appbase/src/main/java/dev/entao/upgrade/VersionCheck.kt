@file:Suppress("unused")

package dev.entao.upgrade

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.Keep
import dev.entao.json.YsonObject
import dev.entao.appbase.App
import dev.entao.appbase.AppFile
import dev.entao.appbase.FileProv
import dev.entao.appbase.Task
import dev.entao.pages.openActivity
import dev.entao.pages.toast
import dev.entao.sql.MapTable
import dev.entao.base.*
import dev.entao.http.HttpGet
import dev.entao.log.loge
import dev.entao.sql.MapConfig
import dev.entao.views.XDialog
import java.io.File

/**
 * Created by entaoyang@163.com on 2017-06-13.
 */

@Keep
class VersionCheck(val jo: YsonObject) {
    val versionCode: Int by jo
    val versionName: String by jo
    val msg: String by jo
    val resId: Int by jo
    val download: String by jo

    fun great(): Boolean {
        return versionCode > App.versionCode
    }

    fun downloadFile(progress: Progress?): File? {
        val f = AppFile.cache.file(App.appName + ".apk")
        val r = HttpGet(this.download).download(f, progress)
        if (r.OK) {
            return f
        }
        return null
    }


    companion object {
        var CHECK_URL: String = "http://app800.cn/am/check"
        var CHECK_HOURS = 4 //最多每4小时检查一次

        private val ignoreMap = MapTable("ver_ignore")

        private var lastCheckUpdate: Long by MapConfig

        private fun isIgnored(verCode: Int): Boolean {
            return ignoreMap[verCode.toString()] != null
        }

        fun checkByUser(ctx: Context) {
            ctx.openActivity(UpgradeActivity::class)
        }

        fun checkAuto(act: Activity) {
            val last = lastCheckUpdate
            val now = System.currentTimeMillis()
            if (now - last < CHECK_HOURS * 60 * 60 * 1000) {
                return
            }
            Task.back {
                val v = check()
                Task.fore {
                    if (v != null) {
                        lastCheckUpdate = now
                        if (!isIgnored(v.versionCode)) {
                            confirmInstall(act, v, true)
                        }
                    }

                }
            }
        }

        private fun confirmInstall(act: Activity, v: VersionCheck, quiet: Boolean) {
            if (!v.great()) {
                try {
                    if (!quiet) {
                        act.toast("已经是最新版本")
                    }
                } catch (ex: Exception) {
                }
                return
            }

            XDialog(act).apply {
                title("检查升级")
                body {
                    if (v.msg.isEmpty()) {
                        text("发现新版本${v.versionName}")
                    } else {
                        text("发现新版本${v.versionName}\n${v.msg}")
                    }
                }
                buttons {
                    cancel("取消")
                    normal("忽略此版本") {
                        ignoreMap.put(v.versionCode.toString(), v.versionName)
                    }
                    ok("升级") {
                        act.openActivity(UpgradeActivity::class)
                    }
                }
                show()
            }
        }


        fun check(): VersionCheck? {
            val r = HttpGet(CHECK_URL).arg("pkg", App.packageName).request()
            if (r.OK) {
                val jo = r.ysonObject() ?: return null
                if (jo.int("code") != 0) {
                    return null
                }
                val jdata = jo.obj("data") ?: return null
                return VersionCheck(jdata)
            }
            return null
        }

        fun checkAndInstallQuiet(context: Context) {
            Task.back {
                val v = check()
                if (v != null) {
                    if (v.great()) {
                        val f = v.downloadFile(null)
                        if (f != null) {
                            Task.fore {
                                App.installApkFile(context, f)
                            }
                        }
                    }
                }
            }
        }


    }
}