@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.json

import dev.entao.base.*
import dev.entao.base.lowerCased
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

class YsonObject(val data: LinkedHashMap<String, YsonValue> = LinkedHashMap(32)) : YsonValue(), Map<String, YsonValue> by data {
    var caseLess = false

    constructor(capcity: Int) : this(LinkedHashMap<String, YsonValue>(capcity))

    constructor(json: String) : this() {
        val p = YsonParser(json)
        val v = p.parse(true)
        if (v is YsonObject) {
            data.putAll(v.data)
        }
    }

    override fun yson(buf: StringBuilder) {
        buf.append("{")
        var first = true
        for ((k, v) in data) {
            if (!first) {
                buf.append(",")
            }
            first = false
            buf.append("\"")
            escapeJsonTo(buf, k)
            buf.append("\":")
            v.yson(buf)
        }
        buf.append("}")
    }

    override fun preferBufferSize(): Int {
        return 256
    }

    override fun toString(): String {
        return yson()
    }

    private val _changedProperties = ArrayList<KMutableProperty<*>>(8)
    private var gather: Boolean = false

    @Synchronized
    fun gather(block: () -> Unit): ArrayList<KMutableProperty<*>> {
        this.gather = true
        this._changedProperties.clear()
        block()
        val ls = ArrayList<KMutableProperty<*>>(_changedProperties)
        this.gather = false
        return ls
    }

    operator fun <V> setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        this.data[property.userName] = Yson.toYson(value)
        if (this.gather) {
            if (property is KMutableProperty) {
                if (property !in this._changedProperties) {
                    this._changedProperties.add(property)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <V> getValue(thisRef: Any?, property: KProperty<*>): V {
        val retType = property.returnType
        val v = if (caseLess) {
            this[property.userName] ?: this[property.userName.lowerCased]
        } else {
            this[property.userName]
        } ?: YsonNull.inst

        if (v !is YsonNull) {
            val pv = YsonDecoder.decodeByType(v, retType, null)
            if (pv != null || retType.isMarkedNullable) {
                return pv as V
            }
        }
        if (retType.isMarkedNullable) {
            return null as V
        }
        val defVal = property.defaultValue
        if (defVal != null) {
            return strToV(defVal, property)
        }
        return defaultValueOfProperty(property)
    }

    fun removeProperty(p: KProperty<*>) {
        this.data.remove(p.userName)
    }

    fun str(key: String, value: String?) {
        if (value == null) {
            data[key] = YsonNull.inst
        } else {
            data[key] = YsonString(value)
        }
    }

    fun str(key: String): String? {
        return when (val v = get(key)) {
            null -> null
            is YsonString -> v.data
            is YsonBool -> v.data.toString()
            is YsonNum -> v.data.toString()
            is YsonNull -> null
            is YsonObject -> v.toString()
            is YsonArray -> v.toString()
            else -> v.toString()
        }
    }

    fun int(key: String, value: Int?) {
        if (value == null) {
            data[key] = YsonNull.inst
        } else {
            data[key] = YsonNum(value)
        }
    }

    fun int(key: String): Int? {
        return when (val v = get(key)) {
            is YsonNum -> v.data.toInt()
            is YsonString -> v.data.toIntOrNull()
            else -> null
        }
    }

    fun long(key: String, value: Long?) {
        if (value == null) {
            data[key] = YsonNull.inst
        } else {
            data[key] = YsonNum(value)
        }
    }

    fun long(key: String): Long? {
        return when (val v = get(key)) {
            is YsonNum -> v.data.toLong()
            is YsonString -> v.data.toLongOrNull()
            else -> null
        }
    }

    fun real(key: String, value: Double?) {
        if (value == null) {
            data[key] = YsonNull.inst
        } else {
            data[key] = YsonNum(value)
        }
    }

    fun real(key: String): Double? {
        return when (val v = get(key)) {
            is YsonNum -> v.data.toDouble()
            is YsonString -> v.data.toDoubleOrNull()
            else -> null
        }
    }

    fun bool(key: String, value: Boolean?) {
        if (value == null) {
            data[key] = YsonNull.inst
        } else {
            data[key] = YsonBool(value)
        }
    }

    fun bool(key: String): Boolean? {
        val v = get(key) ?: return null
        return BoolYsonConverter.fromYsonValue(v)
    }

    fun obj(key: String, value: YsonObject?) {
        if (value == null) {
            data[key] = YsonNull.inst
        } else {
            data[key] = value
        }
    }

    fun obj(key: String, block: YsonObject.() -> Unit) {
        val yo = YsonObject()
        data[key] = yo
        yo.block()
    }

    fun obj(key: String): YsonObject? {
        return get(key) as? YsonObject
    }

    fun arr(key: String, value: YsonArray?) {
        if (value == null) {
            data[key] = YsonNull.inst
        } else {
            data[key] = value
        }
    }

    fun arr(key: String): YsonArray? {
        return get(key) as? YsonArray
    }

    fun arr(key: String, block: YsonArray.() -> Unit): YsonArray {
        val ls = YsonArray()
        data[key] = ls
        ls.block()
        return ls
    }


    fun any(key: String, value: Any?) {
        data[key] = from(value)
    }

    fun any(key: String): Any? {
        return get(key)
    }

    fun putNull(key: String) {
        data[key] = YsonNull.inst
    }

    infix fun <V> String.TO(value: V) {
        any(this, value)
    }


    infix fun String.TO(value: YsonObject) {
        obj(this, value)
    }

    infix fun String.TO(value: YsonArray) {
        arr(this, value)
    }

    companion object {
        init {
            TextConverts[YsonObject::class] = YsonObjectTextConvert
        }
    }
}

object YsonObjectTextConvert : ITextConvert {
    override val defaultValue: Any = YsonObject()
    override fun fromText(text: String): Any {
        return YsonObject(text)
    }
}


@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> KClass<T>.createYsonModel(argValue: YsonObject): T {
//    return this.createInstance(YsonObject::class, argValue)
    val c = this.constructors.first { it.parameters.size == 1 && it.parameters.first().type.classifier == YsonObject::class }
    return c.call(argValue)
}

fun ysonObject(block: YsonObject.() -> Unit): YsonObject {
    val b = YsonObject()
    b.block()
    return b
}

fun YsonObject.pathValue(path: String): YsonValue? {
    val ls = path.split('/')
    var yv: YsonValue? = this
    ls.forEachIndexed { n, s ->
        yv = when (yv) {
            is YsonObject -> (yv as YsonObject)[s]
            is YsonArray -> (yv as YsonArray)[s.toInt()]
            else -> return null
        }
        if (n == ls.size - 1) {
            return yv
        }
    }
    return null
}

// data/user/name
// data/items/0/name
fun YsonObject.pathString(path: String): String? {
    val yv = this.pathValue(path) ?: return null
    return when (yv) {
        is YsonNull -> null
        is YsonString -> yv.data
        is YsonBool -> yv.data.toString()
        is YsonNum -> yv.data.toString()
        is YsonBlob -> yv.encoded
        else -> yv.toString()
    }
}

fun YsonObject.pathArray(path: String): YsonArray? {
    val yv = this.pathValue(path) ?: return null
    return when (yv) {
        is YsonArray -> yv
        else -> null
    }
}

fun YsonObject.pathObject(path: String): YsonObject? {
    val yv = this.pathValue(path) ?: return null
    return when (yv) {
        is YsonObject -> yv
        else -> null
    }
}

fun YsonObject.pathBool(path: String): Boolean? {
    val yv = this.pathValue(path) ?: return null
    return when (yv) {
        is YsonBool -> yv.data
        is YsonString -> yv.data.toBoolean()
        else -> null
    }
}

fun YsonObject.pathNumber(path: String): Number? {
    val yv = this.pathValue(path) ?: return null
    return when (yv) {
        is YsonNum -> yv.data
        is YsonString -> yv.data.toDoubleOrNull()
        else -> null
    }
}

fun YsonObject.pathInt(path: String): Int? {
    return pathNumber(path)?.toInt()
}

fun YsonObject.pathLong(path: String): Long? {
    return pathNumber(path)?.toLong()
}

fun YsonObject.pathDouble(path: String): Double? {
    return pathNumber(path)?.toDouble()
}