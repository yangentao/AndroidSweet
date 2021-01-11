package dev.entao.appbase

import dev.entao.json.Yson
import dev.entao.json.YsonValue
import kotlin.reflect.KClass

open class ItemCoder(private val itemCls: KClass<*>) {

    open fun encoder(item: Any): YsonValue {
        return Yson.toYson(item)
    }

    open fun decoder(yv: YsonValue): Any? {
        return Yson.toModelClass(yv, itemCls)
    }
}