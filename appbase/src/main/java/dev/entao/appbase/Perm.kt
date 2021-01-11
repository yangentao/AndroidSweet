@file:Suppress("unused")

package dev.entao.appbase

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment
import dev.entao.base.BlockUnit
import dev.entao.page.Page
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by entaoyang@163.com on 2016-11-17.
 */

typealias ManiPerm = Manifest.permission

//[240,250] 请求Code范围
open class Perm(private val perms: Set<String>) {

    var onResult: (Map<String, Boolean>) -> Unit = {}

    private val resultMap = HashMap<String, Boolean>()
    private val reqCode: Int = genCode()

    init {
        perms.forEach {
            resultMap[it] = App.hasPerm(it)
        }
    }

    fun req(act: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val set = resultMap.filter { !it.value }.keys
            if (set.isEmpty()) {
                callback()
            } else {
                permStack.add(this)
                act.requestPermissions(set.toTypedArray(), this.reqCode)
            }
        } else {
            callback()
        }
    }

    fun req(f: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val set = resultMap.filter { !it.value }.keys
            if (set.isEmpty()) {
                callback()
            } else {
                permStack.add(this)
                f.requestPermissions(set.toTypedArray(), this.reqCode)
            }
        } else {
            callback()
        }
    }

    private fun callback() {
        perms.forEach {
            resultMap[it] = App.hasPerm(it)
        }
        Task.fore {
            onResult(resultMap)
        }
    }

    companion object {
        private val permStack: LinkedList<Perm> = LinkedList()
        private var code = 250

        @Synchronized
        private fun genCode(): Int {
            if (code > 299) {
                code = 240
            }
            this.code += 1
            return code
        }

        fun onPermResult(requestCode: Int) {
            for (p in permStack) {
                if (p.reqCode == requestCode) {
                    permStack.remove(p)
                    p.callback()
                    return
                }
            }
        }
    }
}

fun App.hasPerm(p: String): Boolean {
    return this.inst.hasPerm(p)
}


fun Context.hasPermSet(ps: Set<String>): Boolean {
    for (p in ps) {
        if (!hasPerm(p)) {
            return false
        }
    }
    return true
}

fun Context.hasPerm(p: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PackageManager.PERMISSION_GRANTED == this.checkSelfPermission(p)
    } else {
        true
    }
}

fun Fragment.hasPerm(p: String): Boolean {
    return App.hasPerm(p)
}


fun Activity.reqPerm(p: String, block: (Boolean) -> Unit) {
    val perm = Perm(hashSetOf(p))
    perm.onResult = {
        block(it[p] ?: true)
    }
    perm.req(this)
}

fun Activity.reqPerm(pSet: Set<String>, block: (Map<String, Boolean>) -> Unit) {
    val perm = Perm(pSet.toHashSet())
    perm.onResult = {
        block(it)
    }
    perm.req(this)
}

fun Fragment.reqPerm(p: String, block: (Boolean) -> Unit) {
    val perm = Perm(hashSetOf(p))
    perm.onResult = {
        block(it[p] ?: true)
    }
    perm.req(this)
}

fun Fragment.reqPerm(pSet: Set<String>, block: (Map<String, Boolean>) -> Unit) {
    val perm = Perm(pSet.toHashSet())
    perm.onResult = {
        block(it)
    }
    perm.req(this)
}


fun Fragment.onPermAllow(perm: String, block: BlockUnit) {
    this.activity?.onPermAllow(perm, block)
}

fun Page.reqPerm(p: String, block: (Boolean) -> Unit) {
    val perm = Perm(hashSetOf(p))
    perm.onResult = {
        block(it[p] ?: true)
    }
    perm.req(this.activity)
}

fun Page.reqPerm(pSet: Set<String>, block: (Map<String, Boolean>) -> Unit) {
    val perm = Perm(pSet.toHashSet())
    perm.onResult = {
        block(it)
    }
    perm.req(this.activity)
}

fun Activity.onPermAllow(perm: String, block: BlockUnit) {
    if (this.hasPerm(perm)) {
        block()
    } else {
        this.reqPerm(perm) {
            if (this.hasPerm(perm)) {
                block()
            }
        }
    }
}



