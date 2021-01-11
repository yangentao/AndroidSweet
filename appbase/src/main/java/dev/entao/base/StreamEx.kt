@file:Suppress("unused")

package dev.entao.base

import dev.entao.appbase.Task
import dev.entao.log.loge
import java.io.*
import java.nio.charset.Charset
import java.util.zip.ZipInputStream

fun <T : Closeable> T.closeSafe() {
    try {
        this.close()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

inline fun <T : Closeable> T.useSafe(block: (T) -> Unit) {
    try {
        block(this)
    } catch (e: Exception) {
        loge(e)
        e.printStackTrace()
    } finally {
        try {
            this.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

inline fun <T : Closeable> T.closeAfter(block: (T) -> Unit) {
    try {
        block(this)
    } catch (e: Exception) {
        loge(e)
        e.printStackTrace()
    } finally {
        try {
            this.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun InputStream.readText(charset: Charset = Charsets.UTF_8): String {
    val bs = this.readBytes()
    return String(bs, charset)
}


fun InputStream.copyToProgress(os: OutputStream, progress: Progress?, total: Int = this.available()) {
    try {
        progress?.also {
            Task.fore {
                it.onProgressStart(total)
            }
        }

        val buf = ByteArray(8192)
        var pre: Long = 0L
        var recv = 0

        var n = this.read(buf)
        while (n >= 0) {
            os.write(buf, 0, n)
            recv += n
            progress?.also { p ->
                val curr = System.currentTimeMillis()
                if (curr - pre > PROGRESS_DELAY) {
                    pre = curr
                    Task.fore {
                        p.onProgress(recv, total, if (total > 0) recv * 100 / total else 0)
                    }
                }
            }
            n = this.read(buf)
        }
        os.flush()
        progress?.also {
            Task.fore {
                it.onProgress(recv, total, if (total > 0) recv * 100 / total else 0)
            }
        }
    } finally {
        progress?.also { p ->
            Task.fore {
                p.onProgressFinish()
            }
        }
    }
}

fun ZipInputStream.unzipFirst(file: File): Boolean {
    val fos = FileOutputStream(file)
    val b = unzipFirst(fos)
    fos.closeSafe()
    return b
}

fun ZipInputStream.unzipFirst(os: OutputStream): Boolean {
    val e = nextEntry
    if (e != null) {
        copyTo(os)
        closeEntry()
        return true
    }
    return false
}

fun ZipInputStream.unzipByName(name: String, os: OutputStream): Boolean {
    var e = nextEntry
    while (e != null) {
        if (e.name == name && !e.isDirectory) {
            copyTo(os)
            closeEntry()
            return true
        }
        closeEntry()
        e = nextEntry
    }
    return false
}