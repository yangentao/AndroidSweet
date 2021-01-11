package dev.entao.upgrade

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import dev.entao.appbase.App
import dev.entao.appbase.FileProv
import dev.entao.appbase.Task
import dev.entao.base.Progress
import dev.entao.base.keepDot
import dev.entao.log.loge
import dev.entao.page.canInstallPackage
import dev.entao.pages.TitledActivity
import dev.entao.theme.ColorX
import dev.entao.theme.heightButton
import dev.entao.views.*
import java.io.File


class UpgradeActivity : TitledActivity(), Progress {

    lateinit var msgView: TextView
    lateinit var upButton: Button
    lateinit var webButton: Button
    var ver: VersionCheck? = null
    var apkFile: File? = null

    override fun onCreateContent(contentView: LinearLayout) {
        titleBar {
            title("升级")
            text("检查") {
                queryVersion()
            }
            showBack {
                finish()
            }
        }

        contentView.apply {
            textView {
                linearParams {
                    widthFill.height(80).margins(20).gravityCenterX
                }
                textSize(20)
                gravityCenter()
                textColorPrimary()
                text = "正在检查是否有新版本..."
                msgView = this
            }
            button {
                linearParams {
                    widthFill.heightButton.gravityCenterX
                }
                styleRound(ColorX.safe)

                text = "升级"
                gone()
                click {
                    clickUp()
                }
                upButton = this
            }

            button {
                linearParams {
                    widthFill.heightButton.gravityCenterX
                }
                styleRound(ColorX.safe)
                text = "用浏览器打开"
                gone()
                click {
                    openWeb()
                }
                webButton = this
            }
        }
        queryVersion()
    }

    private fun openWeb() {
        val url = ver?.download ?: return
        App.openUrl(url)
    }

    private fun sizeText(n: Int): String {
        return when {
            n > 1000_000 -> (n.toDouble() / 1000_000).keepDot(2) + "M"
            n > 1000 -> (n.toDouble() / 1000).keepDot(2) + "K"
            else -> "$n 字节"
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onProgressStart(total: Int) {
        Task.fore {
            msgView.text = "正在下载:共${sizeText(total)}"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onProgress(current: Int, total: Int, percent: Int) {
        Task.fore {
            msgView.text = "正在下载:共${sizeText(total)}, $percent%"
        }
    }

    override fun onProgressFinish() {
        Task.fore {
            msgView.text = "下载完成"
        }
    }

    private fun download() {
        val oldFile = apkFile
        if (oldFile != null) {
            Task.fore {
                install(oldFile)
            }
            return
        }

        val v = ver ?: return
        Task.back {
            val f = v.downloadFile(this@UpgradeActivity)
            Task.fore {
                if (f != null) {
                    apkFile = f
                    install(f)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun install(file: File) {
        App.installApkFile(this, file)
    }

    @SuppressLint("SetTextI18n")
    private fun clickUp() {
        canInstallPackage {
            if (it) {
                download()
            } else {
                msgView.text = "没有权限安装apk文件"
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun queryVersion() {
        Task.back {
            val v = VersionCheck.check()
            Task.fore {
                if (v == null || !v.great()) {
                    msgView.text = "已经是最新版本"
                } else {
                    val m = if (v.msg != v.versionName) {
                        v.msg
                    } else {
                        ""
                    }
                    msgView.text = "发现新版本: ${v.versionName} $m"
                    upButton.visiable()
                    webButton.visiable()
                    ver = v
                }
            }
        }
    }
}