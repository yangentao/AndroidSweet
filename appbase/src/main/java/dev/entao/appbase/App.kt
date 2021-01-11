@file:Suppress(
    "DEPRECATION",
    "MemberVisibilityCanBePrivate",
    "unused",
    "ClassName",
    "ObjectPropertyName"
)

package dev.entao.appbase

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import dev.entao.log.Yog
import dev.entao.log.loge
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.system.exitProcess

/**
 * Created by entaoyang@163.com on 2017-03-31.
 */

object App {
    var themeColor = 0xFF34C4AA.argb

    private var _inst: Application? = null
    val inst: Application
        get() {
            if (_inst == null) {
                Log.e("app", "You Need invoke App.init() first!")
            }
            return _inst!!
        }
    val context: Context get() = inst

    val hasInst: Boolean get() = _inst != null

    fun init(inst: Application) {
        if (_inst != null) {
            return
        }
        this._inst = inst
        Yog.init(inst)
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            ex.printStackTrace()
            Yog.e(ex)
            exitProcess(-1)
        }
    }

    val debug: Boolean by lazy {
        0 != (inst.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
    }

    val resolver: ContentResolver get() = inst.contentResolver

    val contentResolver: ContentResolver get() = inst.contentResolver

    val resource: Resources get() = inst.resources
    val theme: Resources.Theme get() = inst.theme

    val displayMetrics: DisplayMetrics get() = inst.resources.displayMetrics

    val metaData: Bundle
        get() {
            val ai = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            return ai.metaData
        }


    val appInfo: ApplicationInfo get() = inst.applicationInfo

    val iconLauncher: Int get() = inst.applicationInfo.icon

    val iconBitmap: Bitmap?
        get() {
            return getAppIcon(inst.packageManager, inst.packageName)
        }

    val density: Float get() = inst.resources.displayMetrics.density

    val scaledDensity: Float get() = inst.resources.displayMetrics.scaledDensity

    val appName: String get() = inst.applicationInfo.loadLabel(inst.packageManager).toString()

    val packageName: String get() = inst.packageName

    val packageInfo: PackageInfo get() = inst.packageManager.getPackageInfo(inst.packageName, 0)

    val versionCode: Int get() = packageInfo.versionCode

    val versionName: String get() = packageInfo.versionName

    val sdkVersion: Int get() = Build.VERSION.SDK_INT

    val modelBuild: String get() = Build.MODEL


    val screenWidthPx: Int get() = displayMetrics.widthPixels

    val screenHeightPx: Int get() = displayMetrics.heightPixels

    val downloadManager: DownloadManager
        get() {
            return inst.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }

    val telephonyManager: TelephonyManager
        get() {
            return inst.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }


    val powerManager: PowerManager
        get() {
            return inst.getSystemService(Context.POWER_SERVICE) as PowerManager
        }

    val getKeyguardManager: KeyguardManager
        get() {
            return inst.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        }

    val connectivityManager: ConnectivityManager
        get() {
            return inst.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

    val keyguardManager: KeyguardManager
        get() {
            return inst.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        }

    val notificationManager: NotificationManager
        get() {
            return inst.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

    val prefer: Prefer get() = Prefer.G


    fun openOrCreateDatabase(name: String): SQLiteDatabase {
        return inst.openOrCreateDatabase(name, 0, null)
    }


    @Throws(FileNotFoundException::class)
    fun openStream(uri: Uri): InputStream? = contentResolver.openInputStream(uri)


    // 获取ApiKey
    fun metaValue(context: Context, metaKey: String): String? {
        try {
            val ai = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            return ai.metaData.getString(metaKey)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    fun metaValue(metaKey: String): String? {
        return metaValue(inst, metaKey)
    }


    // 单位兆M
    val memLimit: Int by lazy {
        (inst.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
    }

    val isNetworkConnected: Boolean
        get() {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }


    fun systemService(name: String): Any? {
        return inst.getSystemService(name)
    }


    fun showInputMethod(view: View) {
        val imm = inst.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }


    fun installShortcut(
        name: String,
        imageRes: Int,
        cls: Class<*>,
        exKey: String,
        exValue: String
    ) {
        val it = Intent(Intent.ACTION_MAIN)
        it.addCategory(Intent.CATEGORY_LAUNCHER)
        it.setClass(inst, cls)
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (exKey.isNotEmpty()) {
            it.putExtra(exKey, exValue)
        }
        installShortcut(name, imageRes, it)
    }

    @Suppress("DEPRECATION", "LocalVariableName")
    private fun installShortcut(name: String, imageRes: Int, intent: Intent) {
        val ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT"
        val addShortcutIntent = Intent(ACTION_INSTALL_SHORTCUT)
        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false)// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
        addShortcutIntent.putExtra(
            Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
            Intent.ShortcutIconResource.fromContext(inst, imageRes)
        )
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
        inst.sendBroadcast(addShortcutIntent)
    }

    fun installApkFile(context: Context, file: File) {
        val apkUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(file)
        } else {
            FileProv.uriOfFile(file)
        }
        installApkUri(context, apkUri)
    }

    fun installApkUri(context: Context, apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        try {
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun openUrl(url: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.data = Uri.parse(url)
            inst.startActivity(i)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun drawable(@DrawableRes resId: Int): Drawable {
        return ResourcesCompat.getDrawable(resource, resId, inst.theme)!!
    }

    fun color(@ColorRes resId: Int): Int {
        return ResourcesCompat.getColor(resource, resId, inst.theme)
    }

    fun database(name: String): SQLiteDatabase {
        return inst.openOrCreateDatabase(name, 0, null)
    }


    val isNightMode: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                resource.configuration.isNightModeActive
            } else {
                false
            }
        }

    val isLandscape: Boolean get() = resource.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


}

