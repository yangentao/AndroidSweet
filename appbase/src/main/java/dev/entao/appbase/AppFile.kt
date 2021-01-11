package dev.entao.appbase

import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AppFile(val dir: File) {

    fun file(filename: String): File {
        return File(dir, filename)
    }

    fun dir(dirName: String): File {
        return ensureDir(dir, dirName)
    }


    companion object {
        val doc: AppFile = AppFile(App.inst.filesDir)
        val cache: AppFile = AppFile(App.inst.externalCacheDir ?: App.inst.cacheDir)

        val publicImageDir: File by lazy {
            val f: File = File(App.inst.externalCacheDir ?: App.inst.cacheDir, "pubimages")
            if (!f.exists()) {
                f.mkdir()
            }
            f
        }

        fun publicImageFile(filename: String): File {
            return File(publicImageDir, filename)
        }

        fun tempFile(ext: String): File {
            return this.cache.file(makeTempFileName(ext))
        }

        fun ensureDir(root: File, dir: String): File {
            val f = File(root, dir)
            if (!f.exists()) {
                f.mkdirs()
                f.mkdir()
                try {
                    File(f, ".nomedia").createNewFile()
                } catch (e: IOException) {
                    Log.e("app", f.absolutePath)
                    e.printStackTrace()
                }
            }
            return f
        }

        fun makeTempFileName(ext: String = "tmp"): String {
            var dotExt = ".tmp"
            if (ext.isNotEmpty()) {
                dotExt = if (ext[0] == '.') {//.x
                    ext
                } else {
                    ".$ext"
                }
            }
            val fmt = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault())
            val s = fmt.format(Date(System.currentTimeMillis()))
            return s + dotExt
        }

    }

}