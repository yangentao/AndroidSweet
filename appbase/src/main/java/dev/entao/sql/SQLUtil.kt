@file:Suppress("unused")

package dev.entao.sql

import dev.entao.json.*
import dev.entao.base.Prop
import dev.entao.base.userName
import kotlin.reflect.KClass

/**
 * Created by entaoyang@163.com on 2018-07-19.
 */

val Prop.s: String get() = this.userName
val KClass<*>.sqlName: String
	get() {
		return "`" + this.userName + "`"
	}


operator fun StringBuilder.plusAssign(s: String) {
	this.append(s)
}

operator fun StringBuilder.plusAssign(ch: Char) {
	this.append(ch)
}

operator fun StringBuilder.plus(s: String): StringBuilder {
	this.append(s)
	return this
}

operator fun StringBuilder.plus(ch: Char): StringBuilder {
	this.append(ch)
	return this
}


fun stringAnyMapToYson(map: Map<String, Any?>): YsonObject {
	val yo = YsonObject()
	map.forEach {
		yo.any(it.key, it.value)
	}
	return yo
}

fun ysonToMap(yo: YsonObject, map: MutableMap<String, Any?>) {
	yo.forEach {
		val vv: Any? = when (val v = it.value) {
			is YsonNull -> null
			is YsonString -> v.data
			is YsonNum -> v.data
			is YsonObject -> v
			is YsonArray -> v
			else -> v
		}
		map[it.key] = vv
	}
}



