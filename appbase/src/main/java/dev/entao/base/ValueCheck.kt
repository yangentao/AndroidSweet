package dev.entao.base

import android.content.Context
import dev.entao.appbase.App
import dev.entao.pages.toast
import dev.entao.views.XDialog
import dev.entao.views.showAlert


fun checkValues(block: () -> Unit): CheckError? {
    try {
        block()
    } catch (ex: CheckError) {
        return ex
    }
    return null
}


class CheckError(msg: String) : Exception(msg) {
    var value: String = ""
    var label: String = ""

    fun showToast() {
        App.context.toast(label + this.message)
    }

    fun showDialog(context: Context) {
        XDialog(context).showAlert("验证错误", label + this.message)
    }
}

class ValueCheck(val value: String, val label: String) {

    constructor(prop: Prop0) : this(prop.getValue()?.toString() ?: "", prop.userLabel)

    fun notEmpty(): ValueCheck {
        if (value.isEmpty()) {
            error("不能为空")
        }
        return this
    }

    fun length(minLen: Int, maxLen: Int): ValueCheck {
        if (minLen > 0) {
            notEmpty()
        }
        if (value.length !in minLen..maxLen) {
            error("长度需在$minLen 到 $maxLen 之间")
        }
        return this
    }

    fun alpha(): ValueCheck {
        for (ch in value) {
            if (ch !in 'a'..'z' && ch !in 'A'..'Z') {
                error("包含非字母字符")
            }
        }
        return this
    }

    fun numInteger(): ValueCheck {
        for (ch in value) {
            if (ch !in '0'..'9') {
                error("包含非数字字符")
            }
        }
        return this
    }

    fun numFloat(): ValueCheck {
        for (ch in value) {
            if ((ch !in '0'..'9') && ch != '.') {
                error("包含非数字字符")
            }
        }
        return this
    }

    fun alphaNum(): ValueCheck {
        for (ch in value) {
            if (ch !in 'a'..'z' && ch !in 'A'..'Z' && ch !in '0'..'9') {
                error("只能是数字或字母")
            }
        }
        return this
    }

    fun error(msg: String) {
        val e = CheckError(msg)
        e.value = value
        e.label = label
        throw e
    }

}