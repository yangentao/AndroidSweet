@file:Suppress("unused")

package dev.entao.sql

import dev.entao.base.Prop
import dev.entao.base.Prop1
import dev.entao.base.fullName
import dev.entao.base.userName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Created by entaoyang@163.com on 2018-07-19.
 */


class SQLQuery {

    private var distinct = false
    private val selectArr = arrayListOf<String>()
    private val fromArr = arrayListOf<String>()
    private var whereClause: String = ""
    private var limitClause: String = ""
    private var joinClause = ""
    private var onClause = ""
    private var orderClause = ""
    private var groupByClause: String = ""
    private var havingClause: String = ""

    val args: ArrayList<Any> = ArrayList()

    val sqlArgs: Array<String>
        get() {
            return args.map(Any::toString).toTypedArray()
        }

    fun groupBy(s: String): SQLQuery {
        groupByClause = "GROUP BY $s"
        return this
    }

    fun groupBy(p: Prop): SQLQuery {
        return this.groupBy(p.s)
    }

    fun having(s: String): SQLQuery {
        havingClause = "HAVING $s"
        return this
    }

    fun having(w: Where): SQLQuery {
        this.having(w.value)
        this.args.addAll(w.args)
        return this
    }
    fun distinct(): SQLQuery {
        this.distinct = true
        return this
    }

    fun selectAll(): SQLQuery {
        selectArr.add("*")
        return this
    }

    fun select(vararg cols: KProperty<*>): SQLQuery {
        cols.mapTo(selectArr) { it.userName }
        return this
    }

    fun select(vararg cols: String): SQLQuery {
        selectArr.addAll(cols)
        return this
    }

    fun from(vararg clses: KClass<*>): SQLQuery {
        clses.mapTo(fromArr) { it.userName }
        return this
    }

    fun from(vararg tables: String): SQLQuery {
        fromArr.addAll(tables.map {
            if (it.startsWith("`")) {
                it
            } else {
                "`$it`"
            }
        })
        return this
    }
    fun join(vararg tables: String): SQLQuery {
        return this.join(tables.toList())
    }

    fun join(vararg modelClasses: KClass<*>): SQLQuery {
        return join(modelClasses.map { it.sqlName })
    }

    fun join(tables: List<String>, joinType: String = "LEFT"): SQLQuery {
        joinClause = "$joinType JOIN ( ${tables.joinToString(", ")} ) "
        return this
    }

    fun on(s: String): SQLQuery {
        onClause = " ON ($s) "
        return this
    }

    fun on(block: OnBuilder.() -> String): SQLQuery {
        val b = OnBuilder()
        val s = b.block()
        return on(s)
    }

    fun where(block: () -> Where): SQLQuery {
        val w = block.invoke()
        return where(w)
    }

    fun where(w: Where?): SQLQuery {
        if (w != null) {
            whereClause = "WHERE ${w.value}"
            args.addAll(w.args)
        }
        return this
    }

    fun where(w: String, vararg params: Any): SQLQuery {
        whereClause = "WHERE $w"
        args.addAll(params)
        return this
    }
    fun asc(col: String): SQLQuery {
        if (orderClause.isEmpty()) {
            orderClause = "ORDER BY $col ASC"
        } else {
            orderClause += ", $col ASC"
        }
        return this
    }

    fun desc(col: String): SQLQuery {
        if (orderClause.isEmpty()) {
            orderClause = "ORDER BY $col DESC"
        } else {
            orderClause += ", $col DESC"
        }
        return this
    }

    fun asc(p: KProperty<*>): SQLQuery {
        return asc(p.s)
    }

    fun desc(p: KProperty<*>): SQLQuery {
        return desc(p.s)
    }

    fun limit(size: Int): SQLQuery {
        return this.limit(size, 0)
    }

    fun limit(size: Int, offset: Int): SQLQuery {
        if (size > 0 && offset >= 0) {
            limitClause = "LIMIT $size OFFSET $offset "
        }
        return this
    }

    fun toCountSQL(): String {
        val sb = StringBuilder(256)

        val dist = if (distinct) {
            " DISTINCT "
        } else {
            ""
        }

        if (selectArr.isEmpty()) {
            sb.append("SELECT COUNT($dist *) ").append(" ")
        } else {
            sb.append("SELECT COUNT($dist " + selectArr.joinToString(",")).append(") ")
        }
        sb.append("FROM ").append(fromArr.joinToString(",")).append(" ")

        if (joinClause.isEmpty()) {
            onClause = ""
        }
        if (groupByClause.isEmpty()) {
            havingClause = ""
        }
        val ls = listOf(joinClause, onClause, whereClause, groupByClause, havingClause, orderClause, limitClause)
        sb += ls.map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
        return sb.toString()
    }

    fun toSQL(): String {
        val sb = StringBuilder(256)
        sb += "SELECT "
        if (distinct) {
            sb += "DISTINCT "
        }

        sb += if (selectArr.isEmpty()) {
            "*"
        } else {
            selectArr.joinToString(", ")
        }
        sb.append(" FROM ").append(fromArr.joinToString(","))
        sb += " "
        if (joinClause.isEmpty()) {
            onClause = ""
        }
        if (groupByClause.isEmpty()) {
            havingClause = ""
        }
        val ls = listOf(joinClause, onClause, whereClause, groupByClause, havingClause, orderClause, limitClause)
        sb += ls.map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
        return sb.toString()
    }

}


class OnCond(val value: String) {
    override fun toString(): String {
        return value
    }
}

infix fun KProperty<*>.EQ(value: KProperty<*>): OnCond {
    val s = this.fullName
    val s2 = value.fullName
    return OnCond("$s=$s2")
}

infix fun KProperty<*>.AS(value: String): String {
    return "${this.userName} AS $value"
}


class OrderBy {

    val orderArr = arrayListOf<String>()

    fun asc(p: KProperty<*>): OrderBy {
        orderArr.add(p.userName + " ASC")
        return this
    }

    fun desc(p: KProperty<*>): OrderBy {
        orderArr.add(p.userName + " DESC")
        return this
    }

    fun orderBy(col: String, ascDesc: String): OrderBy {
        orderArr.add("$col $ascDesc")
        return this
    }

    fun asc(col: String): OrderBy {
        orderArr.add("$col ASC")
        return this
    }

    fun desc(col: String): OrderBy {
        orderArr.add("$col DESC")
        return this
    }
}

class OnBuilder {

    infix fun Prop1.EQ(s: Prop1): String {
        return "${this.s} = ${s.s}"
    }

    infix fun String.EQ(s: String): String {
        return "$this = $s"
    }

    infix fun String.AND(s: String): String {
        return "$this AND $s"
    }
}