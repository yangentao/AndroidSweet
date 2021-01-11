@file:Suppress("DEPRECATION", "unused")

package dev.entao.views

import android.app.ProgressDialog
import android.content.Context
import dev.entao.appbase.Task

class SpinProgressDlg(context: Context) {
    @Suppress("MemberVisibilityCanBePrivate")
    val dlg: ProgressDialog = ProgressDialog(context)

    init {
        dlg.setCancelable(true)
        dlg.setCanceledOnTouchOutside(false)
    }

    fun cancelable(cancelable: Boolean): SpinProgressDlg {
        Task.mainThread {
            dlg.setCancelable(cancelable)
        }
        return this
    }

    fun dismiss(): SpinProgressDlg {
        Task.mainThread {
            dlg.dismiss()
        }
        return this
    }

    fun showLoading(): SpinProgressDlg {
        show("加载中...")
        return this
    }

    fun show(msg: String): SpinProgressDlg {
        Task.mainThread {
            if (dlg.isShowing) {
                dlg.dismiss()
            }
            dlg.setMessage(msg)
            dlg.show()
        }
        return this
    }

    fun msg(msg: String): SpinProgressDlg {
        Task.mainThread {
            dlg.setMessage(msg)
        }
        return this
    }

}
