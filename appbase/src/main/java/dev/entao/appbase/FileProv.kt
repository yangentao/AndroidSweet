@file:Suppress("unused")

package dev.entao.appbase

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import dev.entao.sql.MapTable
import java.io.File
import java.io.FileNotFoundException

class FileProv : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun attachInfo(context: Context?, info: ProviderInfo?) {
        super.attachInfo(context, info)
        AUTHORITY = info?.authority ?: ""
    }


    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val proj = projection ?: COLUMNS
        val file = fileOfUri(uri) ?: return null

        val cols = ArrayList<String>(proj.size)
        val values = ArrayList<Any>(proj.size)
        for (col in proj) {
            if (OpenableColumns.DISPLAY_NAME == col) {
                cols.add(OpenableColumns.DISPLAY_NAME)
                values.add(file.name)
            } else if (OpenableColumns.SIZE == col) {
                cols.add(OpenableColumns.SIZE)
                values.add(file.length())
            }
        }

        val cursor = MatrixCursor(cols.toTypedArray(), 1)
        cursor.addRow(values)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        val file = fileOfUri(uri) ?: return null
        val lastDot = file.name.lastIndexOf('.')
        if (lastDot >= 0) {
            val extension = file.name.substring(lastDot + 1)
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            if (mime != null) {
                return mime
            }
        }
        return "application/octet-stream"
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("No external inserts")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("No external updates")
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val file = fileOfUri(uri) ?: return 0
        return if (file.delete()) 1 else 0

    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val file = fileOfUri(uri)
        val fileMode = modeToMode(mode)
        return ParcelFileDescriptor.open(file, fileMode)
    }

    //  content://xx.xx.files/full_path_hex_encoded
    companion object {
        private var AUTHORITY: String = ""
        private val COLUMNS = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        private val mapTable: MapTable by lazy { MapTable("fileprovider") }
        @Synchronized
        fun uriOfFile(file: File): Uri {
            val path: String = file.canonicalPath
            var key: String = mapTable.findKey(path) ?: ""
            if (key.isEmpty()) {
                var i: Long = 0
                do {
                    key = (i + System.currentTimeMillis()).toString()
                    i += 1
                } while (mapTable.has(key))
            }
            mapTable.put(key, path)
            return Uri.Builder().scheme("content").authority(AUTHORITY).path(key).build()
        }

        @Synchronized
        fun fileOfUri(uri: Uri): File? {
            val path = uri.path ?: return null
            if (path.length < 2) {
                return null
            }
            val s = path.substring(1).substringBefore('/')
            val ss = mapTable[s] ?: return null
            return File(ss)
        }


        private fun modeToMode(mode: String): Int {
            var modeBits: Int = ParcelFileDescriptor.MODE_READ_ONLY
            if ("w" in mode) {
                modeBits = modeBits or ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE
            }
            if ("t" in mode) {
                modeBits = modeBits or ParcelFileDescriptor.MODE_TRUNCATE
            }
            if ("a" in mode) {
                modeBits = modeBits or ParcelFileDescriptor.MODE_APPEND
            }
            return modeBits
        }


    }
}
