package dev.entao.base

import java.sql.Date
import java.sql.Time
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty

/**
 * Created by entaoyang@163.com on 2018/4/4.
 */

interface ITextConvert {
    fun fromText(text: String): Any?
    fun toText(value: Any): String {
        return value.toString()
    }

    val defaultValue: Any
}

val TextConverts = hashMapOf<KClassifier, ITextConvert>(
    String::class to StringText,
    Boolean::class to BoolText,
    java.lang.Boolean::class to BoolText,
    Byte::class to ByteText,
    java.lang.Byte::class to ByteText,
    Short::class to ShortText,
    java.lang.Short::class to ShortText,
    Char::class to CharText,
    java.lang.Character::class to CharText,
    Int::class to IntText,
    java.lang.Integer::class to IntText,
    Long::class to LongText,
    java.lang.Long::class to LongText,
    Float::class to FloatText,
    java.lang.Float::class to FloatText,
    Double::class to DoubleText,
    java.lang.Double::class to DoubleText,
    java.sql.Date::class to SQLDateText,
    java.sql.Time::class to SQLTimeText,
    java.util.Date::class to UtilDateText


)

object StringText : ITextConvert {
    override val defaultValue: Any = ""
    override fun fromText(text: String): Any {
        return text
    }
}

object BoolText : ITextConvert {
    override val defaultValue: Any = false
    override fun fromText(text: String): Any {
        return text == "true"
    }
}

object ByteText : ITextConvert {
    override val defaultValue: Any = 0
    override fun fromText(text: String): Any? {
        return text.toByteOrNull()
    }
}

object ShortText : ITextConvert {
    override val defaultValue: Any = 0
    override fun fromText(text: String): Any? {
        return text.toShortOrNull()
    }
}

object CharText : ITextConvert {
    override val defaultValue: Any = 0
    override fun fromText(text: String): Any? {
        return text.firstOrNull()
    }
}

object IntText : ITextConvert {
    override val defaultValue: Any = 0
    override fun fromText(text: String): Any? {
        return text.toIntOrNull()
    }
}

object LongText : ITextConvert {
    override val defaultValue: Any = 0
    override fun fromText(text: String): Any? {
        return text.toLongOrNull()
    }
}

object FloatText : ITextConvert {
    override val defaultValue: Any = 0
    override fun fromText(text: String): Any? {
        return text.toFloatOrNull()
    }
}

object DoubleText : ITextConvert {
    override val defaultValue: Any = 0
    override fun fromText(text: String): Any? {
        return text.toDoubleOrNull()
    }
}


//yyy-MM-dd
object SQLDateText : ITextConvert {
    override val defaultValue: Any = java.sql.Date(0)
    override fun fromText(text: String): Any? {
        return MyDate.parseDate(text)?.toDateSQL
    }

    override fun toText(value: Any): String {
        return MyDate((value as Date).time).formatDate()
    }
}

//"HH:mm:ss"
object SQLTimeText : ITextConvert {
    override val defaultValue: Any = java.sql.Time(0)
    override fun fromText(text: String): Any? {
        return MyDate.parseTime(text)?.toTimeSQL
    }

    override fun toText(value: Any): String {
        return MyDate((value as Time).time).formatTime()
    }
}

//"yyyy-MM-dd HH:mm:ss.SSS"
object UtilDateText : ITextConvert {


    override val defaultValue: Any = java.util.Date(0)
    override fun fromText(text: String): Any? {
        return MyDate.parseDateTimeX(text)?.toDate
    }

    override fun toText(value: Any): String {
        return MyDate((value as java.util.Date).time).formatDateTimeX()
    }
}


@Suppress("UNCHECKED_CAST")
fun <V> defaultValueOfProperty(p: KProperty<*>): V {
    val retType = p.returnType

    val c = TextConverts[retType.classifier]
    if (c != null) {
        return c.defaultValue as V
    }

    throw IllegalArgumentException("不支持的类型modelMap: ${p.fullName}")
}

@Suppress("UNCHECKED_CAST")
fun <V> strToV(v: String, property: KProperty<*>): V {
    val retType = property.returnType
    if (retType.isTypeString) {
        return v as V
    }
    val c = TextConverts[retType.classifier]
    if (c != null) {
        return c.fromText(v) as V
    }
    throw IllegalArgumentException("不支持的类型${property.fullName}")
}


