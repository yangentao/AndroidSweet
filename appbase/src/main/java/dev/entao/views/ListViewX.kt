@file:Suppress("unused")

package dev.entao.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import kotlin.reflect.KClass


interface ListCallback : AbsListCallback {
    override fun onNewView(context: Context, position: Int): View {
        return ListViewX.makeTextItemView(context)
    }


}

interface ListClickCallback : AdapterClickCallback {
    fun onClickHeader(position: Int, view: View) {}
    fun onClickFooter(position: Int, view: View) {}
    fun onClickItem(position: Int, view: View) {}
}


fun ViewGroup.listViewX(block: ListViewX.() -> Unit): ListViewX {
    return append {
        this.block()
    }
}

open class ListViewX(context: Context) : ListView(context) {

    protected val items = ArrayList<Any>(128)
    private val itemsBack = ArrayList<Any>()
    private var types: List<KClass<*>> = emptyList()
    var typeCallback: AdapterTypeCallback? = null
    var callback: ListCallback? = null
    var clickCallback: ListClickCallback? = null

    private val itemClickListener = OnItemClickListener { _, view, position, _ -> onItemClick(view, position) }

    init {
        this.adapter = MyAdapter()
        onItemClickListener = itemClickListener
    }

    protected open fun onItemClick(view: View, position: Int) {
        val countAdapter = adapter.count
        var pos = position
        if (pos in 0 until headerViewsCount) {
            clickCallback?.onClickHeader(pos, view)
        }
        pos -= headerViewsCount
        if (pos in 0 until countAdapter) {
            onClickAdapter(pos, view, getItem(pos))
            clickCallback?.onClickAdapter(pos, getItem(pos))
        }
        pos -= countAdapter
        if (pos in 0 until footerViewsCount) {
            clickCallback?.onClickFooter(pos, view)
        }
        clickCallback?.onClickItem(position, view)
    }

    protected open fun onClickAdapter(position: Int, view: View, item: Any) {

    }

    fun itemTypes(vararg cs: KClass<*>) {
        types = cs.toList()
    }

    val itemCount: Int get() = items.size

    fun getItem(position: Int): Any {
        return items[position]
    }

    fun setItems(itemList: Collection<Any>) {
        this.items.clear()
        this.items.addAll(itemList)
        this.notifyDataSetChanged()
        callback?.onItemsChanged()
    }

    fun notifyDataSetChanged() {
        (this.adapter as BaseAdapter).notifyDataSetChanged()
    }

    val inFilter: Boolean get() = itemsBack.isNotEmpty()

    fun filter(block: (Any) -> Boolean) {
        if (itemsBack.isEmpty()) {
            itemsBack.addAll(items)
        }
        val ls = itemsBack.filter(block)
        setItems(ls)
    }

    fun clearFilter() {
        if (itemsBack.isNotEmpty()) {
            setItems(itemsBack)
            itemsBack.clear()
        }
    }


    protected open fun onNewView(context: Context, position: Int): View {
        return callback?.onNewView(context, position) ?: AppCompatTextView(context).apply {
            styleListItemView()
            layoutParams = Params.list.widthFill.heightWrap
        }
    }

    protected open fun onBindView(itemView: View, item: Any) {
        callback?.onBindView(itemView, item) ?: if (itemView is TextView) {
            itemView.text = item.toString()
        }
    }

    protected open fun onAdapterView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: onNewView(parent.context, position)
        onBindView(v, getItem(position))
        return v
    }

    private inner class MyAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): Any {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return callback?.onItemId(position) ?: position.toLong()
        }

        override fun getViewTypeCount(): Int {
            return typeCallback?.onAdapterTypeCount() ?: if (types.isEmpty()) 1 else types.size
        }

        override fun getItemViewType(position: Int): Int {
            return typeCallback?.onAdapterViewType(position, getItem(position)) ?: types.indexOf(getItem(position)::class)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return onAdapterView(position, convertView, parent)
        }

    }

    companion object {
        fun makeTextItemView(context: Context): AppCompatTextView {
            return AppCompatTextView(context).apply {
                styleListItemView()
                layoutParams = Params.list.widthFill.heightWrap
            }
        }
    }

}