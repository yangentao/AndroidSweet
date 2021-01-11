@file:Suppress("unused")

package dev.entao.sql


/**
 * Created by entaoyang@163.com on 2017-03-10.
 */
//"cid": 0,
//"name": "locale",
//"type": "TEXT",
//"notnull": 0,
//"dflt_value": null,
//"pk": 0
class TableInfoItem {
    var cid: Int = 0
    var name: String = ""
    var type: String = ""
    var notNull: Boolean = false
    var defaultValue: String? = null
    var pk: Boolean = false

    override fun toString(): String {
        return "$cid, $name, $type, $notNull, $defaultValue, $pk"
    }
}