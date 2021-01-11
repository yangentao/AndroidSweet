@file:Suppress("unused")

package dev.entao.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckedTextView
import dev.entao.appbase.*
import dev.entao.log.fatalIf
import dev.entao.app.R


fun ViewGroup.listViewCheckable(block: ListViewCheckable.() -> Unit): ListViewCheckable {
    return append {
        this.block()
    }
}

class ListViewCheckable(context: Context) : ListViewX(context) {
    private val checkMap: HashMap<String, Any> = HashMap()
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

    override fun onClickAdapter(position: Int, view: View, item: Any) {
        if (view !is CheckableItemView) return
        if (checkCallback.onItemCheckable(item)) {
            view.toggle()
            check(view.isChecked, item)
        }
    }


    override fun onAdapterView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val checkView = if (convertView is CheckableItemView) {
            convertView
        } else {
            CheckableItemView(parent.context).bind(onNewView(parent.context, position))
        }
        onBindView(checkView.view, item)
        if (checkCallback.onItemCheckable(item)) {
            checkView.checkView.visiable()
            checkView.isChecked = checkMap.containsKey(checkCallback.onItemKey(item))
        } else {
            checkView.checkView.invisiable()
            checkView.isChecked = false
        }
        return checkView
    }


}


class CheckableItemView(context: Context) : LinearLayout(context), Checkable {
    val checkView: AppCompatCheckedTextView = AppCompatCheckedTextView(context).needId()
    lateinit var view: View

    init {
        horizontal()
        gravityCenterVertical()
        backColorWhiteFade()
        padding(0)
        this.minimumHeight = 50.dp
        this.layoutParams = Params.margin.widthFill.heightWrap

        val d = StateList.resDrawable(R.mipmap.yet_checkbox) {
            checked(R.mipmap.yet_checkbox_checked)
        }
        checkView.rightImage = d.mutate().sized(20)
        checkView.imagePadding = 0
        addView(checkView, Params.linear.wrap.gravityRightCenter.margins(10, 0, 10, 0))

    }

    fun bind(view: View): CheckableItemView {
        fatalIf(this.childCount >= 2, "已经绑定了view")
        this.view = view
        addView(view, 0, Params.linear.flexX.heightWrap)
        return this
    }

    override fun isChecked(): Boolean {
        return checkView.isChecked
    }

    override fun setChecked(checked: Boolean) {
        if (checked == isChecked) {
            return
        }
        checkView.isChecked = checked
    }

    override fun toggle() {
        checkView.toggle()
    }

}