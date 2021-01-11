package dev.entao.appbase

import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import dev.entao.media.MediaInfo
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


fun Uri.appendId(id: Long): Uri {
    return ContentUris.withAppendedId(this, id)
}

fun Uri.appendPath(pathSegment: String): Uri {
    return Uri.withAppendedPath(this, Uri.encode(pathSegment))
}

fun Uri.appendParam(key: String, value: String): Uri {
    return this.buildUpon().appendQueryParameter(key, value).build()
}

fun Uri.arg(key: String, value: String): Uri {
    return appendParam(key, value)
}

fun Uri.parseId(): Long {
    return ContentUris.parseId(this)
}


fun Uri.openInputStream(): InputStream? {
    if (this.scheme == "content") {
        return App.contentResolver.openInputStream(this)
    }
    val p = this.path ?: return null
    return FileInputStream(p)
}

fun ResUri(resId: Int): Uri {
    return Uri.parse("android.resource://" + App.packageName + "/" + resId.toString())
}

fun FileUri(file: File): Uri {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Uri.fromFile(file)
    } else {
        FileProv.uriOfFile(file)
    }
}


val File.uriLocal: Uri get() = FileUri(this)


val Uri.extName: String?
    get() {
        val s = this.fileName
        if (s != null) {
            val ext = s.substringAfterLast('.', "")
            if (ext.isNotEmpty()) {
                return ext
            }
        }
        return null
    }
val Uri.mimeType: String?
    get() {
        return uriMime(this)
    }
val Uri.fileName: String?
    get() {
        return uriFileName(this)
    }


fun uriFileName(uri: Uri): String? {
    if (uri.scheme == "content") {
        if (uri.host == "media") {
            return MediaInfo(uri).displayName
        }
    }
    return uri.lastPathSegment
}

fun uriMime(uri: Uri): String? {
    if (uri.scheme == "content") {
        if (uri.host == "media") {
            return MediaInfo(uri).mimeType
        }
    }
    val filename = uri.lastPathSegment ?: return null
    val n = filename.lastIndexOf('.')
    if (n > 0) {
        val ext = filename.substring(n + 1)
        if (ext.isNotEmpty()) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        }
    }
    return null
}