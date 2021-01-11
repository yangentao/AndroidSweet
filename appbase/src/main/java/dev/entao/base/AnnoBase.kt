@file:Suppress("unused")

package dev.entao.base

import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class KeepMembers

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class KeepNames


@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Label(val value: String)


@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Name(val value: String, val forDB: String = "", val forJson: String = "")

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultValue(val value: String)


//@FormOptions("0:OK","1:BAD")
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FormOptions(vararg val options: String)

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Required


enum class ExcludeFor {
    ALL, SQL, JSON
}


@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Exclude(val value: ExcludeFor = ExcludeFor.ALL)

//字段长度--字符串
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Length(val value: Int)

inline fun <reified T : Annotation> KAnnotatedElement.hasAnnotation(): Boolean = null != this.findAnnotation<T>()

val KProperty<*>.userName: String
    get() {
        return this.findAnnotation<Name>()?.value ?: this.name
    }
val KProperty<*>.fullName: String
    get() {
        val clsName = this.javaField?.declaringClass?.kotlin?.userName
        val fname = this.findAnnotation<Name>()?.value ?: this.name
        return clsName!! + "." + fname
    }

val KClass<*>.userName: String
    get() {
        return this.findAnnotation<Name>()?.value ?: this.simpleName!!
    }

val KParameter.userName: String
    get() {
        return this.findAnnotation<Name>()?.value ?: this.name
        ?: throw IllegalStateException("参数没有名字")
    }

val KFunction<*>.userName: String
    get() {
        return this.findAnnotation<Name>()?.value ?: this.name
    }

val KClass<*>.userLabel: String
    get() {
        return this.findAnnotation<Label>()?.value ?: this.simpleName!!
    }


val KProperty<*>.labelValue: String?
    get() {
        return this.findAnnotation<Label>()?.value
    }
val KProperty<*>.userLabel: String
    get() {
        return this.findAnnotation<Label>()?.value ?: this.userName
    }

val KFunction<*>.labelValue: String?
    get() {
        return this.findAnnotation<Label>()?.value
    }
val KFunction<*>.userLabel: String
    get() {
        return this.findAnnotation<Label>()?.value ?: this.userName
    }

val KProperty<*>.defaultValue: String?
    get() {
        return this.findAnnotation<DefaultValue>()?.value
    }


val KProperty0<*>.selectLabel: String
    get() {
        val map = this.selectOptionsStatic
        val v = this.getValue()?.toString() ?: ""
        return map[v] ?: ""
    }

val KProperty<*>.selectOptionsStatic: Map<String, String>
    get() {
        val fs = this.findAnnotation<FormOptions>() ?: return emptyMap()
        return FormSelectCache.find(fs)
    }

private object FormSelectCache {
    private val map = HashMap<FormOptions, LinkedHashMap<String, String>>()

    fun find(fs: FormOptions): Map<String, String> {
        val al = map[fs]
        if (al != null) {
            return al
        }

        val lMap = LinkedHashMap<String, String>()
        fs.options.forEach {
            val kv = it.split(":")
            if (kv.size == 2) {
                lMap[kv[0]] = kv[1]
            } else if (kv.size == 1) {
                lMap[kv[0]] = kv[0]
            }
        }
        map[fs] = lMap
        return lMap
    }
}