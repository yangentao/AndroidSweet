@file:Suppress("unused")

package dev.entao.sql

import android.content.ContentValues
import dev.entao.json.*
import java.io.File

/**
 * Created by entaoyang@163.com on 2017-03-10.
 */


fun mapToContentValues(map: Map<String, Any?>): ContentValues {
    val c = ContentValues(map.size + 1)
    map.forEach {
        c.putAny(it.key, it.value)
    }
    return c
}

fun ContentValues.putAny(name: String, value: Any?) {
    if (value == null) {
        this.putNull(name)
        return
    }
    when (value) {
        is YsonNull -> this.putNull(name)
        is YsonString -> this.put(name, value.data)
        is YsonBool -> this.put(name, if (value.data) 1 else 0)
        is YsonNum -> {
            when (value.data) {
                is Double -> this.put(name, value.data.toDouble())
                is Float -> this.put(name, value.data.toFloat())
                is Long -> this.put(name, value.data.toLong())
                is Int -> this.put(name, value.data.toInt())
                is Short -> this.put(name, value.data.toShort())
                is Byte -> this.put(name, value.data.toByte())
                else -> this.put(name, value.data.toDouble())
            }
        }
        is YsonBlob -> this.put(name, value.data)
        is YsonArray -> this.put(name, value.toString())

        is Boolean -> this.put(name, if (value) 1 else 0)
        is Byte -> this.put(name, value.toLong())
        is Short -> this.put(name, value.toLong())
        is Int -> this.put(name, value.toLong())
        is Long -> this.put(name, value.toLong())
        is Float -> this.put(name, value.toDouble())
        is Double -> this.put(name, value.toDouble())
        is ByteArray -> this.put(name, value)
        is String -> this.put(name, value)
        is File -> this.put(name, value.absolutePath)
        is Char -> this.put(name, value.toString())
        else -> this.put(name, value.toString())
    }

}
