@file:Suppress("unused")

package dev.entao.base

import kotlin.jvm.internal.CallableReference
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.*
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod


typealias Prop = KProperty<*>
typealias Prop0 = KProperty0<*>
typealias Prop1 = KProperty1<*, *>


fun KType.isClass(kcls: KClass<*>): Boolean {
    return this.classifier == kcls
}

val KType.isTypeString: Boolean get() = this.isClass(String::class)
val KType.isTypeInt: Boolean get() = this.isClass(Int::class) || this.isClass(java.lang.Integer::class)
val KType.isTypeLong: Boolean get() = this.isClass(Long::class) || this.isClass(java.lang.Long::class)
val KType.isTypeByte: Boolean get() = this.isClass(Byte::class) || this.isClass(java.lang.Byte::class)
val KType.isTypeShort: Boolean get() = this.isClass(Short::class) || this.isClass(java.lang.Short::class)
val KType.isTypeChar: Boolean get() = this.isClass(Char::class) || this.isClass(java.lang.Character::class)
val KType.isTypeBoolean: Boolean get() = this.isClass(Boolean::class) || this.isClass(java.lang.Boolean::class)
val KType.isTypeFloat: Boolean get() = this.isClass(Float::class) || this.isClass(java.lang.Float::class)
val KType.isTypeDouble: Boolean get() = this.isClass(Double::class) || this.isClass(java.lang.Double::class)
val KType.isTypeByteArray: Boolean get() = this.isClass(ByteArray::class)


val KType.genericArgs: List<KTypeProjection> get() = this.arguments.filter { it.variance == KVariance.INVARIANT }
val KType.isGeneric: Boolean get() = this.arguments.isNotEmpty()


val KFunction<*>.ownerClass: KClass<*>?
    get() {
        if (this is FunctionReference) {
            if (this.boundReceiver != CallableReference.NO_RECEIVER) {
                return this.boundReceiver::class
            }
            val c = this.owner as? KClass<*>
            if (c != null) {
                return c
            }
        } else {
            return this.javaMethod?.declaringClass?.kotlin
        }
        return null
    }
val KFunction<*>.ownerObject: Any?
    get() {
        if (this is FunctionReference) {
            if (this.boundReceiver != CallableReference.NO_RECEIVER) {
                return this.boundReceiver
            }
        }
        return null
    }


val KProperty<*>.ownerClass: KClass<*>?
    get() {
        if (this is CallableReference) {
            if (this.boundReceiver != CallableReference.NO_RECEIVER) {
                return this.boundReceiver::class
            }
            val c = this.owner as? KClass<*>
            if (c != null) {
                return c
            }
        } else {
            return this.javaField?.declaringClass?.kotlin
        }

        return null
    }

val KProperty<*>.ownerObject: Any?
    get() {
        if (this is CallableReference) {
            if (this.boundReceiver != CallableReference.NO_RECEIVER) {
                return this.boundReceiver::class
            }
        }
        return null
    }


val KFunction<*>.paramName1: String?
    get() {
        return this.valueParameters.firstOrNull()?.name
    }


/*
 * class A{
 *      val set:HashSet<String> = HashSet()
 * }
 *
 * A::set.firstGenericType => String::class
 *
 */
val KProperty<*>.firstGenericType: KClass<*>? get() = this.returnType.arguments.firstOrNull()?.type?.classifier as? KClass<*>


val KProperty<*>.isTypeString: Boolean get() = this.returnType.isTypeString
val KProperty<*>.isTypeInt: Boolean get() = this.returnType.isTypeInt
val KProperty<*>.isTypeLong: Boolean get() = this.returnType.isTypeLong
val KProperty<*>.isTypeByte: Boolean get() = this.returnType.isTypeByte
val KProperty<*>.isTypeShort: Boolean get() = this.returnType.isTypeShort
val KProperty<*>.isTypeChar: Boolean get() = this.returnType.isTypeChar
val KProperty<*>.isTypeBoolean: Boolean get() = this.returnType.isTypeBoolean
val KProperty<*>.isTypeFloat: Boolean get() = this.returnType.isTypeFloat
val KProperty<*>.isTypeDouble: Boolean get() = this.returnType.isTypeDouble
val KProperty<*>.isTypeByteArray: Boolean get() = this.returnType.isTypeByteArray
val KProperty<*>.isTypeHashSet: Boolean get() = this.returnType.isClass(HashSet::class)
val KProperty<*>.isTypeArrayList: Boolean get() = this.returnType.isClass(ArrayList::class)

fun KProperty<*>.isClass(cls: KClass<*>): Boolean {
    return this.returnType.isClass(cls)
}

val KProperty<*>.isTypeEnum: Boolean
    get() {
        return this.javaField?.type?.isEnum ?: false
    }

fun KProperty<*>.getValue(): Any? {
    if (this.getter.parameters.isEmpty()) {
        return this.getter.call()
    }
    return null
}

fun KProperty<*>.getValue(inst: Any): Any? {
    if (this.getter.parameters.isEmpty()) {
        return this.getter.call()
    }
    return this.getter.call(inst)
}

fun KProperty<*>.getBindValue(): Any? {
    if (this.getter.parameters.isEmpty()) {
        return this.getter.call()
    }
    return null
}

fun KMutableProperty<*>.setValue(inst: Any, value: Any?) {
    this.setter.call(inst, value)
}

val KProperty<*>.isPublic: Boolean get() = this.visibility == KVisibility.PUBLIC


inline fun <reified T : Any> KClass<T>.createInstance(argCls: KClass<*>, argValue: Any): T {
    val c = this.constructors.first { it.parameters.size == 1 && it.parameters.first().type.classifier == argCls }
    return c.call(argValue)
}


fun Any.javaSetFieldValue(name: String, value: Any?) {
    val f = this.javaClass.getField(name) ?: error("No field found! $name ")
    f.isAccessible = true
    f.set(this, value)
}