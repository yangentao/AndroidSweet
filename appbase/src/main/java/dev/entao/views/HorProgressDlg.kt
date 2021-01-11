@file:Suppress("DEPRECATION", "unused")

package dev.entao.views

import android.app.ProgressDialog
import android.content.Context
import dev.entao.appbase.InMainThread
import dev.entao.appbase.Task

class HorProgressDlg(context: Context) {
    @Suppress("MemberVisibilityCanBePrivate")
    val dlg: ProgressDialog = ProgressDialog(context)
    var value = 0
        private set
    private var preTime: Long = 0
    private var cache = true

    init {
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dlg.setCanceledOnTouchOutside(false)
        dlg.setCancelable(true)
        dlg.max = 100
    }

    fun setCancelable(cancelable: Boolean): HorProgressDlg {
        dlg.setCancelable(cancelable)
        return this
    }

    fun setTitle(title: String?): HorProgressDlg {
        if (InMainThread) {
            dlg.setTitle(title)
        } else {
            Task.fore { dlg.setTitle(title) }
        }
        return this
    }

    fun show(title: String? = null): HorProgressDlg {
        if (InMainThread) {
            show2(title)
        } else {
            Task.fore {
                show2(title)
            }
        }
        return this

    }

    private fun show2(title: String? = null): HorProgressDlg {
        if (dlg.isShowing) {
            dlg.dismiss()
        }
        dlg.max = 100
        dlg.progress = 0
        if (InMainThread) {
            dlg.setTitle(title)
            dlg.show()
        } else {
            Task.fore {
                dlg.setTitle(title)
                dlg.show()
            }
        }
        return this
    }

    fun dismiss(): HorProgressDlg {
        if (InMainThread) {
            dlg.dismiss()
        } else {
            Task.fore {
                dlg.dismiss()
            }
        }
        return this
    }

    val max: Int
        get() = dlg.max

    fun setMax(max: Long): HorProgressDlg {
        if (InMainThread) {
            dlg.max = max.toInt()
            dlg.progress = 0
        } else {
            Task.fore {
                dlg.max = max.toInt()
                dlg.progress = 0
            }
        }
        return this
    }

    fun setValue(progress: Int): HorProgressDlg {
        if (InMainThread) {
            setValue2(progress)
        } else {
            Task.fore {
                setValue2(progress)
            }
        }
        return this
    }

    private fun setValue2(progress: Int): HorProgressDlg {
        this.value = progress
        if (!cache) {
            dlg.progress = this.value
        } else {
            if (value == 0 || value == dlg.max) {
                dlg.progress = this.value
            } else {
                val cur = System.currentTimeMillis()
                if (cur - preTime > 100) {
                    dlg.progress = value
                    preTime = cur
                }
            }
        }
        return this
    }

    fun setCacheable(cache: Boolean): HorProgressDlg {
        this.cache = cache
        return this
    }

}
