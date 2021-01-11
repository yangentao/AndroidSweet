package dev.entao.other

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import dev.entao.appbase.App
import dev.entao.sql.MapTable
import dev.entao.log.logd

class ApkDownReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        logd("下载完成 Receiver ID: ", id)
        if (id == -1L) {
            return
        }
        val url = downMap.getString(id.toString()) ?: return
        logd("URL: ", url)
        val uriLocal = App.downloadManager.getUriForDownloadedFile(id) ?: return
        logd(uriLocal)
        App.installApkUri(context, uriLocal)
    }


    companion object {
        val downMap = MapTable("downloadTasks")
        private var downReceiver: ApkDownReceiver? = null

        private fun regDownRecv() {
            if (downReceiver != null) {
                return
            }
            val d = ApkDownReceiver()
            downReceiver = d
            App.inst.registerReceiver(d, IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"))
        }

        //        this.reqPerm(ManiPerm.WRITE_EXTERNAL_STORAGE) {
//            logd("请求写download文件夹 $it ")
//            if (it) {
//                val a = "http://app800.cn/am/res/download?id=47"
//                ApkDownReceiver.downloadAndInstall(a, "下载APK")
//            }
//        }
        fun downloadAndInstall(url: String, title: String, desc: String = "") {

            val req = DownloadManager.Request(Uri.parse(url))
            if (title.isNotEmpty()) {
                req.setTitle(title)
            }
            if (desc.isNotEmpty()) {
                req.setDescription(desc)
            }
            //req.setVisibleInDownloadsUi(true)
            req.setAllowedOverRoaming(true)
            req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${App.appName}.apk")


            val downId = App.downloadManager.enqueue(req)
            logd("DownloadTask enqueue: ", downId)
            downMap.put(downId.toString(), url)
            regDownRecv()

        }

    }
}
