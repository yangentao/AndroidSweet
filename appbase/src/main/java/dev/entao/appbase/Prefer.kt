package dev.entao.appbase

import android.content.SharedPreferences
import dev.entao.base.userName
import kotlin.reflect.KProperty

/**
 *
 */
class Prefer(val name: String) {

    private var sp: SharedPreferences = App.inst.getSharedPreferences(name, 0)

    fun edit(block: SharedPreferences.Editor.() -> Unit): Boolean {
        val a = sp.edit()
        a.block()
        return a.commit()
    }

    fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    @Synchronized
    fun setIfNotPresent(key: String, value: Boolean): Boolean {
        val exist = sp.contains(key)
        if (!exist) {
            sp.edit().putBoolean(key, value).apply()
        }
        return exist
    }


    fun getBool(key: String, defValue: Boolean): Boolean {
        return sp.getBoolean(key, defValue)
    }


    fun getInt(key: String, defValue: Int): Int {
        return sp.getInt(key, defValue)
    }


    fun getLong(key: String, defValue: Long): Long {
        return sp.getLong(key, defValue)
    }

    fun getString(key: String): String? {
        return sp.getString(key, null)
    }

    fun getString(key: String, defValue: String): String {
        return sp.getString(key, null) ?: defValue
    }


    fun getFloat(key: String, defValue: Float): Float {
        return sp.getFloat(key, defValue)
    }

    companion object {
        val G: Prefer by lazy { Prefer("global_prefer") }
    }

}

class PreferBool(
    private val key: String? = null, private val defValue: Boolean = false,
    private val prefer: Prefer = Prefer.G, val validCallback: (Boolean) -> Boolean = { true }
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return prefer.getBool(key ?: property.userName, defValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        if (validCallback(value)) {
            prefer.edit {
                putBoolean(key ?: property.userName, value)
            }
        }
    }
}

class PreferInt(
    private val key: String? = null,
    private val defValue: Int = 0,
    private val prefer: Prefer = Prefer.G,
    val validCallback: (Int) -> Boolean = { true }
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return prefer.getInt(key ?: property.userName, defValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        if (validCallback(value)) {
            prefer.edit {
                putInt(key ?: property.userName, value)
            }
        }
    }
}

class PreferLong(
    private val key: String? = null,
    private val defValue: Long = 0,
    private val prefer: Prefer = Prefer.G,
    val validCallback: (Long) -> Boolean = { true }
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return prefer.getLong(key ?: property.userName, defValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        if (validCallback(value)) {
            prefer.edit {
                putLong(key ?: property.userName, value)
            }
        }
    }
}

class PreferString(private val key: String? = null, private val defValue: String = "", private val prefer: Prefer = Prefer.G, val validCallback: (String) -> Boolean = { true }) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return prefer.getString(key ?: property.userName) ?: defValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        if (validCallback(value)) {
            prefer.edit {
                putString(key ?: property.userName, value)
            }
        }
    }
}

class PreferStringX(
    private val key: String? = null,
    private val defValue: String? = null,
    private val prefer: Prefer = Prefer.G,
    private val validCallback: (String?) -> Boolean = { true }
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return prefer.getString(key ?: property.userName) ?: defValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        if (validCallback(value)) {
            if (value == null) {
                prefer.edit { remove(key ?: property.userName) }
            } else {
                prefer.edit { putString(key ?: property.userName, value) }
            }
        }
    }
}
