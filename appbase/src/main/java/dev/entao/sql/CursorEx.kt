@file:Suppress("unused")

package dev.entao.sql

import android.database.Cursor
import dev.entao.json.*
import dev.entao.base.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties


inline fun Cursor.eachRow(block: (Cursor) -> Unit) {
    this.use {
        while (it.moveToNext()) {
            block(it)
        }
    }
}

inline fun Cursor.firstRow(block: (Cursor) -> Unit) {
    this.use {
        if (it.moveToNext()) {
            block(it)
        }
    }
}


//带下划线表示关闭Cursor
val Cursor.listRowData: List<RowData>
    get() {
        val ls = ArrayList<RowData>()
        this.use {
            while (this.moveToNext()) {
                ls += this.currentRowData
            }
        }
        return ls
    }

//带下划线表示关闭Cursor
val Cursor.firstRowData: RowData?
    get() {
        var d: RowData? = null
        this.use {
            if (this.moveToNext()) {
                d = this.currentRowData
            }
        }
        return d
    }

val Cursor.currentRowData: RowData
    get() {
        return RowData.rowOf(this)
    }


//带下划线表示关闭Cursor
val Cursor.listYsonObject: List<YsonObject>
    get() {
        val ls = ArrayList<YsonObject>()
        this.use {
            while (this.moveToNext()) {
                ls += this.currentYsonObject
            }
        }
        return ls
    }

//带下划线表示关闭Cursor
val Cursor.firstYsonObject: YsonObject?
    get() {
        var d: YsonObject? = null
        this.use {
            if (this.moveToNext()) {
                d = this.currentYsonObject
            }
        }
        return d
    }

val Cursor.currentYsonObject: YsonObject
    get() {
        val map = YsonObject(32)
        val c = this
        val colCount = c.columnCount
        for (i in 0 until colCount) {
            val key = c.getColumnName(i)
            val v: YsonValue = when (c.getType(i)) {
                Cursor.FIELD_TYPE_NULL -> YsonNull.inst
                Cursor.FIELD_TYPE_INTEGER -> YsonNum(c.getLong(i))
                Cursor.FIELD_TYPE_FLOAT -> YsonNum(c.getDouble(i))
                Cursor.FIELD_TYPE_STRING -> YsonString(c.getString(i))
                Cursor.FIELD_TYPE_BLOB -> YsonBlob(c.getBlob(i))
                else -> YsonNull.inst
            }
            map.data[key] = v
        }
        return map
    }


//Person(val yo:YsonObject)
inline fun <reified T : Any> Cursor.listYsonModels(): List<T> {
    val ls = ArrayList<T>(256)
    this.eachRow {
        val yo = it.currentYsonObject
        ls += T::class.createInstance(YsonObject::class, yo)
    }
    return ls
}

//Person(val yo:YsonObject)
inline fun <reified T : Any> Cursor.firstYsonModel(): T? {
    this.firstRow {
        val yo = it.currentYsonObject
        return T::class.createInstance(YsonObject::class, yo)
    }
    return null
}

fun Cursor.firstString(index: Int = 0): String? {
    this.firstRow {
        return it.getString(index)
    }
    return null
}

fun Cursor.firstLong(index: Int = 0): Long? {
    this.firstRow {
        return it.getLong(index)
    }
    return null
}

fun Cursor.firstInt(index: Int = 0): Int? {
    this.firstRow {
        return it.getInt(index)
    }
    return null
}


fun <T : Any> Cursor.firstModel(block: () -> T): T? {
    this.firstRow {
        val m = block()
        fillModel(m)
        return m
    }
    return null
}

inline fun <reified T : Any> Cursor.firstModel(): T? {
    this.firstRow {
        val m = T::class.createInstance()
        fillModel(m)
        return m
    }
    return null
}

inline fun <reified T : Any> Cursor.listModels(): List<T> {
    val ps = T::class.propsOfModel
    val ls = ArrayList<T>(256)
    this.eachRow {
        val m = T::class.createInstance()
        fillModel(m, ps)
        ls += m
    }
    return ls
}

inline fun <reified T : Any> Cursor.listModels(block: () -> T): List<T> {
    val ps = T::class.propsOfModel
    val ls = ArrayList<T>(256)
    this.eachRow {
        val m = block()
        fillModel(m, ps)
        ls += m
    }
    return ls
}


fun Cursor.fillModel(model: Any, ps: List<KMutableProperty1<*, *>>) {
    val c = this
    val colCount = c.columnCount
    for (i in 0 until colCount) {
        val key = c.getColumnName(i)
        val p = ps.firstOrNull {
            it.userName == key
        } ?: continue
        val ptype = p.returnType
        val v: Any? = when (c.getType(i)) {
            Cursor.FIELD_TYPE_INTEGER -> {
                if (ptype.isTypeLong) {
                    c.getLong(i)
                } else {
                    c.getInt(i)
                }
            }
            Cursor.FIELD_TYPE_FLOAT -> {
                if (ptype.isTypeDouble) {
                    c.getDouble(i)
                } else {
                    c.getFloat(i)
                }
            }
            Cursor.FIELD_TYPE_STRING -> c.getString(i)
            Cursor.FIELD_TYPE_BLOB -> c.getBlob(i)
            else -> null
        }
        if (v != null || p.returnType.isMarkedNullable) {
            p.setValue(model, v)
        }
    }
}

fun Cursor.fillModel(model: Any) {
    this.fillModel(model, model::class.propsOfModel)
}

val KClass<*>.propsOfModel: List<KMutableProperty1<*, *>>
    get() {
        return this.memberProperties.filter {
            it is KMutableProperty1<*, *>
                    && !it.hasAnnotation<Exclude>()
                    && it.isPublic
        }.map { it as KMutableProperty1<*, *> }
    }


