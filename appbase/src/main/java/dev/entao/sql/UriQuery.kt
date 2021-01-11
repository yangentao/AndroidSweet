@file:Suppress("unused", "MemberVisibilityCanBePrivate", "PropertyName")

package dev.entao.sql

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import dev.entao.appbase.App
import java.util.*

/**
 * Created by entaoyang@163.com on 16/5/13.
 */

class UriQuery(val uri: Uri) {
    val ID = "_id"

    private var columns = ArrayList<String>()

    private var orderStr: String = ""
    private var limitStr: String = ""
    private var whereStr: String = ""
    private var argList = ArrayList<Any>()

    constructor(uri: Uri, id: Long) : this(ContentUris.withAppendedId(uri, id))

    fun colunms(vararg cols: String): UriQuery {
        columns.addAll(cols.toList())
        return this
    }

    fun where(w: Where?): UriQuery {
        if (w != null) {
            this.whereStr = w.toString()
            this.argList.addAll(w.args)
        }
        return this
    }

    fun where(s: String, args: List<Any> = emptyList()): UriQuery {
        this.whereStr = s
        this.argList.addAll(args)
        return this
    }


    fun whereId(_id: Long): UriQuery {
        return where("$ID = $_id")
    }

    fun limit(limit: Int): UriQuery {
        if (limit > 0) {
            limitStr = " LIMIT $limit "
        }
        return this
    }

    fun limit(limit: Int, offset: Int): UriQuery {
        if (limit > 0 && offset >= 0) {
            limitStr = " LIMIT $limit OFFSET $offset "
        }
        return this
    }

    fun orderBy(sortOrder: String): UriQuery {
        this.orderStr = sortOrder
        return this
    }

    fun orderBy(col: String, asc: Boolean): UriQuery {
        this.orderStr = col + if (asc) " ASC " else " DESC "
        return this
    }

    fun asc(col: String): UriQuery {
        return orderBy(col, true)
    }

    fun desc(col: String): UriQuery {
        return orderBy(col, false)
    }

    fun query(): Cursor? {
        val od = if (orderStr.isEmpty()) {
            "_id ASC $limitStr"
        } else {
            "$orderStr $limitStr"
        }
        try {
            return App.contentResolver.query(
                uri,
                columns.toTypedArray(),
                whereStr,
                argList.map { it.toString() }.toTypedArray(),
                od
            )
        } catch (ex: Exception) {
        }
        return null
    }

    fun queryOne(): Cursor? {
        limit(1)
        return query()
    }


    companion object {
        fun select(uri: Uri, vararg cols: String): UriQuery {
            return UriQuery(uri).colunms(*cols)
        }
    }

}