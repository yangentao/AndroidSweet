@file:Suppress("unused", "ObjectLiteralToLambda")

package dev.entao.pages

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dev.entao.appbase.InMainThread
import dev.entao.appbase.Task
import dev.entao.base.MyDate
import dev.entao.log.loge
import dev.entao.page.Page
import java.lang.ref.WeakReference
import kotlin.reflect.KClass


/**
 * Created by entaoyang@163.com on 16/5/23.
 */
fun Context.openActivity(n: Intent) {
    try {
        this.startActivity(n)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}


fun Fragment.openActivity(cls: KClass<out Activity>) {
    val n = Intent(act, cls.java)
    act.openActivity(n)
}

fun Fragment.openActivity(cls: KClass<out Activity>, block: Intent.() -> Unit) {
    val n = Intent(act, cls.java)
    n.block()
    act.openActivity(n)
}

fun Fragment.openActivity(cls: Class<out Activity>) {
    val n = Intent(act, cls)
    act.openActivity(n)
}

fun Fragment.openActivity(cls: Class<out Activity>, block: Intent.() -> Unit) {
    val n = Intent(act, cls)
    n.block()
    act.openActivity(n)
}

fun Context.openActivity(cls: KClass<out Activity>) {
    val n = Intent(this, cls.java)
    this.openActivity(n)
}

fun Context.openActivity(cls: KClass<out Activity>, block: Intent.() -> Unit) {
    val n = Intent(this, cls.java)
    n.block()
    this.openActivity(n)
}

fun Context.openActivity(cls: Class<out Activity>) {
    val n = Intent(this, cls)
    this.openActivity(n)
}

fun Context.openActivity(cls: Class<out Activity>, block: Intent.() -> Unit) {
    val n = Intent(this, cls)
    n.block()
    this.openActivity(n)
}


fun Activity.setWindowFullScreen(full: Boolean = true) {
    if (full) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    } else {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}


val AppCompatActivity.fragMgr: FragmentManager
    get() {
        return this.supportFragmentManager
    }


val Fragment.fragMgr: FragmentManager
    get() {
        return this.parentFragmentManager
    }

fun AppCompatActivity.trans(block: FragmentTransaction.() -> Unit) {
    val b = fragMgr.beginTransaction()
    b.block()
    b.commitAllowingStateLoss()
}


fun Fragment.trans(block: FragmentTransaction.() -> Unit) {
    val b = fragMgr.beginTransaction()
    b.block()
    b.commitAllowingStateLoss()
}


val AppCompatActivity.currentFragment: Fragment? get() = fragMgr.fragments.lastOrNull()


val Fragment.act: FragmentActivity get() = this.requireActivity()


fun Fragment.softInputAdjustResize() {
    act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}

fun Fragment.hideInputMethod() {
    val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (imm.isActive && act.currentFocus != null) {
        imm.hideSoftInputFromWindow(
            act.currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun Activity.hideInputMethod() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val v = this.currentFocus ?: return
    if (imm.isActive) {
        imm.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

fun Fragment.showInputMethod() {
    val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // 显示或者隐藏输入法
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Fragment.toast(vararg texts: String) {
    this.activity?.toast(*texts)
}

fun Page.toast(vararg texts: String) {
    this.activity.toast(*texts)
}

fun Context.toast(vararg texts: String) {
    if (InMainThread) {

//        val tv = TextView(this).needId().styleMajor()
//        tv.textSizeB()
//        tv.padding(20, 10, 20, 10)
////        tv.isSingleLine = false
//        tv.style {
//            fill(0x88000000L.argb)
//            corners(4)
//            text(Color.WHITE)
//        }
//        tv.elevation = 6.dpf
//        tv.text = texts.joinToString("")

        val t = Toast.makeText(this, texts.joinToString(""), Toast.LENGTH_LONG)
//        t.view = tv
        t.setGravity(Gravity.CENTER, 0, 0)
        t.show()

    } else {
        Task.fore {
            this.toast(*texts)
        }
    }
}

fun Context.toastLong(vararg texts: String): Toast {
    val t = Toast.makeText(this, texts.joinToString(""), Toast.LENGTH_LONG)
    t.setGravity(Gravity.CENTER, 0, 0)
    t.show()
    return t
}

fun Context.toastShort(vararg texts: String): Toast {
    val t = Toast.makeText(this, texts.joinToString(""), Toast.LENGTH_SHORT)
    t.setGravity(Gravity.CENTER, 0, 0)
    t.show()
    return t
}

object ToastMgr {
    val map = HashMap<String, WeakReference<Toast>>()
    private fun clean() {
        val ls = map.keys
        for (k in ls) {
            if (map[k]?.get() == null) {
                map.remove(k)
            }
        }
    }

    fun put(key: String, t: Toast) {
        map.remove(key)?.get()?.cancel()
        map[key] = WeakReference(t)
        clean()
    }
}

fun Context.toastKey(key: String, vararg texts: String): Toast {
    val t = Toast.makeText(this, texts.joinToString(""), Toast.LENGTH_LONG)
    t.setGravity(Gravity.CENTER, 0, 0)
    ToastMgr.put(key, t)
    t.show()
    return t
}

fun Fragment.toastKey(key: String, vararg texts: String): Toast {
    return this.requireActivity().toastKey(key, *texts)
}

fun Fragment.viewImage(uri: Uri) {
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.action = Intent.ACTION_VIEW
    intent.setDataAndType(uri, "image/*")
    startActivity(intent)
}

fun Context.viewImage(uri: Uri) {
    this.viewAction(uri, "image/*")
}

fun Context.viewAction(uri: Uri, dataType: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.setDataAndType(uri, dataType)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.openActivity(intent)
}


fun Context.viewUrl(uri: Uri) {
    val it = Intent(Intent.ACTION_VIEW, uri)
    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    openActivity(it)
}

fun Context.openApk(uri: Uri) {
    try {
        val i = Intent(Intent.ACTION_VIEW)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.setDataAndType(uri, "application/vnd.android.package-archive")
        startActivity(i)
    } catch (e: Exception) {
        this.viewUrl(uri)
    }

}


fun Fragment.smsTo(phoneSet: Set<String>, body: String = "") {
    if (phoneSet.isNotEmpty()) {
        smsTo(phoneSet.joinToString(";"), body)
    }
}

fun Fragment.smsTo(phone: String, body: String = "") {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone"))
    if (body.isNotEmpty()) {
        intent.putExtra("sms_body", body)
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    act.startActivity(intent)
}


fun Fragment.dial(phone: String) {
    try {
        val uri = Uri.fromParts("tel", phone, null)
        val it = Intent(Intent.ACTION_DIAL, uri)
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        act.startActivity(it)
    } catch (e: Throwable) {
        loge(e)
    }
}


val Context.configuration: Configuration
    get() = resources.configuration

val Context.isPortrait: Boolean get() = this.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
val Context.isLandscape: Boolean get() = this.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


fun Fragment.pickDate(date: MyDate, block: (MyDate) -> Unit) {
    this.pickDate(date.year, date.month, date.day, block)
}

fun Fragment.pickDate(oldYear: Int, oldMonth: Int, oldDay: Int, block: (MyDate) -> Unit) {
    val dlg = DatePickerDialog(act, { _, year, month, dayOfMonth ->
        val date = MyDate(0L)
        date.year = year
        date.month = month
        date.day = dayOfMonth
        block(date)
    }, oldYear, oldMonth, oldDay)
    dlg.show()
}


fun Fragment.pickTime(date: MyDate, block: (MyDate) -> Unit) {
    this.pickTime(date.hour, date.minute, block)
}

fun Fragment.pickTime(oldHour: Int, oldMinute: Int, block: (MyDate) -> Unit) {
    val dlg = TimePickerDialog(act, { _, hour, minute ->
        val date = MyDate(0L)
        date.hour = hour
        date.minute = minute
        block(date)
    }, oldHour, oldMinute, true)
    dlg.show()
}

