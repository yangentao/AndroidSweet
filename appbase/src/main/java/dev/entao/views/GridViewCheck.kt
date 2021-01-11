package dev.entao.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import dev.entao.theme.ColorX


fun ViewGroup.gridViewCheck(block: GridViewCheck.() -> Unit): GridViewCheck {
    return append {
        this.block()
    }
}

class GridViewCheck(context: Context) : GridViewX(context) {
    private val checkMap: HashMap<String, Any> = HashMap()

    var itemBackColorNormal: Int = Color.TRANSPARENT
    var itemBackColorChecked: Int = ColorX.blueDark

    var checkCallback: ItemCheckCallback = object : ItemCheckCallback {}

    val isAllChecked: Boolean get() = items.isNotEmpty() && checkMap.size == items.size
    val checkedCount: Int get() = checkMap.size
    val checkedItems: List<Any>
        get() {
            return checkMap.values.toList()
        }

    fun isChecked(item: Any): Boolean {
        return checkCallback.onItemKey(item) in checkMap
    }

    fun check(ck: Boolean, ls: List<Any>) {
        for (item in ls) {
            if (checkCallback.onItemCheckable(item)) {
                val key = checkCallback.onItemKey(item)
                if (ck) {
                    checkMap[key] = item
                } else {
                    checkMap.remove(key)
                }
                checkCallback.onCheckChanged(item)
            }
        }
        notifyDataSetChanged()

    }

    fun check(ck: Boolean, item: Any) {
        if (checkCallback.onItemCheckable(item)) {
            val key = checkCallback.onItemKey(item)
            if (ck) {
                checkMap[key] = item
            } else {
                checkMap.remove(key)
            }
            checkCallback.onCheckChanged(item)
        }
        notifyDataSetChanged()

    }

    override fun onItemClick(view: View, position: Int) {
        val item = getItem(position)
        if (checkCallback.onItemCheckable(item)) {
            if (isChecked(item)) {
                check(false, item)
            } else {
                check(true, item)
            }
        } else {
            super.onItemClick(view, position)
        }
    }

    override fun onBindView(itemView: View, item: Any) {
        super.onBindView(itemView, item)
        if (isChecked(item)) {
            itemView.backColor(itemBackColorChecked)
        } else {
            itemView.backColor(itemBackColorNormal)
        }
    }
}
