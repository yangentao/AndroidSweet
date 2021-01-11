@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.sql

import android.util.Log
import dev.entao.json.YsonArray
import dev.entao.json.YsonObject
import dev.entao.appbase.App
import dev.entao.base.*
import java.util.*
import kotlin.collections.HashSet
import kotlin.reflect.KProperty

/**
 * Created by entaoyang@163.com on 2017-03-24.
 */

@KeepMembers
class MapTable(val table: String) {

    init {
        synchronized(nameSet) {
            if (table !in nameSet) {
                nameSet.add(table)
                db.createTable(table, "$KEY TEXT PRIMARY KEY", "$VAL TEXT")
                db.createIndex(table, "value")
            }
        }
    }

    operator fun <V> setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        this.put(property.userName, value)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <V> getValue(thisRef: Any?, property: KProperty<*>): V {
        val v = get(property.userName) ?: property.defaultValue
        ?: return if (property.returnType.isMarkedNullable) {
            null as V
        } else {
            defaultValueOfProperty(property)
        }
        return strToV(v, property)
    }

    fun trans(block: (MapTable) -> Unit) {
        db.trans {
            block(this@MapTable)
        }
    }

    fun toHashMap(): HashMap<String, String> {
        val map = HashMap<String, String>(512)
        toMap(map)
        return map
    }

    fun toMap(map: MutableMap<String, String>) {
        val c = db.query("select $KEY, $VAL from $table", emptyList()) ?: return
        c.listRowData.forEach {
            val k = it.str(KEY)!!
            val v = it.str(VAL) ?: ""
            map[k] = v
        }
    }

    fun putAll(map: Map<String, String>) {
        this.trans {
            for ((k, v) in map) {
                db.replaceX(table, "key" to k, "value" to v)
            }
        }

    }

    fun findKey(value: String): String? {
        val c = db.query("select key from $table where value = ? limit 1", listOf(value)) ?: return null
        val m = c.listRowData.firstOrNull() ?: return null
        return m.str(KEY)
    }

    fun has(key: String): Boolean {
        val c = db.query("select value from $table where key=? limit 1", listOf(key))
            ?: return false
        return c.listRowData.isNotEmpty()
    }

    operator fun get(key: String): String? {
        val c = db.query("select value from $table where key=? limit 1", listOf(key))
            ?: return null
        val m = c.listRowData.firstOrNull() ?: return null
        return m.str(VAL)
    }

    operator fun set(key: String, value: String?) {
        if (value == null) {
            remove(key)
        } else {
            db.replaceX(table, "key" to key, "value" to value)
        }
    }

    fun getString(key: String): String? {
        return get(key)
    }

    fun putString(key: String, value: String?) {
        set(key, value)
    }

    fun put(key: String, value: Any?) {
        return set(key, value?.toString())
    }

    fun getInt(key: String): Int? {
        return getString(key)?.toIntOrNull()
    }

    fun getLong(key: String): Long? {
        return getString(key)?.toLongOrNull()
    }

    fun getDouble(key: String): Double? {
        return getString(key)?.toDoubleOrNull()
    }

    fun getBool(key: String): Boolean? {
        return getString(key)?.toBoolean()
    }

    fun getYsonObject(key: String): YsonObject? {
        val s = this.getString(key) ?: return null
        return YsonObject(s)
    }

    fun getYsonArray(key: String): YsonArray? {
        val s = this.getString(key) ?: return null
        return YsonArray(s)
    }

    fun remove(key: String): Int {
        return db.delete(table, "$KEY = ?", arrayOf(key))
    }

    fun removeAll(): Int {
        return db.delete(table, null, null)
    }

    fun dumpAll() {
        val map = toHashMap()
        for ((k, v) in map) {
            Log.d("SQL", "$k = $v")
        }

    }

    companion object {
        const val KEY = "key"
        const val VAL = "value"
        private val nameSet = HashSet<String>()

        private var db = App.openOrCreateDatabase("maptable.db")

        val config = MapTable("global_config_map_table")
    }
}

val MapConfig = MapTable("global_config_map_table")
