@file:Suppress("unused")

package dev.entao.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.entao.appbase.Task
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by yet on 2015/10/28.
 * val rv = RecyView(context)
 * rv.layoutGrid(2)
 * rv.divider()
 * rv.itemTypes(Person::class,Company::class)
 * rv.setItems(ls)
 */


fun ViewGroup.recyView(block: RecyView.() -> Unit): RecyView {
    return append {
        this.block()
    }
}

interface RecyCallback {
    fun onRecyNewView(context: Context, viewType: Int): View
    fun onRecyBindView(itemView: View, position: Int)
    fun onRecyItemClick(itemView: View, position: Int) {}
    fun onRecyItemsChanged() {}
    fun onRecyRequestItems(args: Any?): List<Any> = emptyList()
    fun getRecyItemId(position: Int): Long {
        return position.toLong()
    }
}

interface RecyItemTypeCallback {
    fun onRecyItemViewType(position: Int): Int
}


open class RecyView(context: Context) : RecyclerView(context) {
    private val items = ArrayList<Any>()
    private val itemsBack = ArrayList<Any>()
    private var itemTypes: List<KClass<*>> = emptyList()
    var callback: RecyCallback? = null
    var typeCallback: RecyItemTypeCallback? = null

    init {
        this.adapter = RecyAdapter()
    }


    fun itemTypes(vararg ls: KClass<*>) {
        itemTypes = ls.toList()
    }

    fun layoutLinear(): LinearLayoutManager {
        val lm = LinearLayoutManager(context, VERTICAL, false)
        this.layoutManager = lm
        return lm
    }

    fun layoutLinearHorizontal(): LinearLayoutManager {
        val lm = LinearLayoutManager(context, HORIZONTAL, false)
        this.layoutManager = lm
        return lm
    }

    fun layoutGrid(columns: Int): GridLayoutManager {
        val gm = GridLayoutManager(context, columns, VERTICAL, false)
        this.layoutManager = gm
        return gm
    }

    fun layoutGridHorizontal(columns: Int): GridLayoutManager {
        val gm = GridLayoutManager(context, columns, HORIZONTAL, false)
        this.layoutManager = gm
        return gm
    }

    fun divider() {
        addItemDecoration(DividerItemDecoration(context, VERTICAL))
    }

    fun dividerHorizontal() {
        addItemDecoration(DividerItemDecoration(context, HORIZONTAL))
    }

    fun notifyDataSetChanged() {
        this.adapter?.notifyDataSetChanged()
    }

    fun setItems(ls: Collection<Any>) {
        this.items.clear()
        this.items.addAll(ls)
        adapter?.notifyDataSetChanged()
        callback?.onRecyItemsChanged()
    }


    fun getItem(position: Int): Any {
        return items[position]
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getItemX(position: Int): T {
        return items[position] as T
    }

    val itemCount: Int
        get() = items.size

    fun findView(position: Int): View? {
        return this.layoutManager?.findViewByPosition(position)
    }

    fun filter(block: (Any) -> Boolean) {
        if (itemsBack.isEmpty()) {
            itemsBack.addAll(items)
        }
        val ls = itemsBack.filter(block)
        setItems(ls)
    }

    fun clearFilter() {
        if (itemsBack.isNotEmpty()) {
            this.items.clear()
            this.items.addAll(itemsBack)
            adapter!!.notifyDataSetChanged()
            itemsBack.clear()
            callback?.onRecyItemsChanged()
        }
    }

    fun requestItems(args: Any?) {
        val cb = callback ?: return
        Task.back {
            val ls = cb.onRecyRequestItems(args)
            Task.fore {
                setItems(ls)
            }
        }
    }


    inner class RecyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            callback?.onRecyItemClick(v, adapterPosition)
        }
    }

    private inner class RecyAdapter : RecyclerView.Adapter<RecyHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyHolder {
            val view = callback?.onRecyNewView(parent.context, viewType) ?: TextView(parent.context)
            if (view.layoutParams == null) {
                when (val lm = layoutManager) {
                    is GridLayoutManager -> {
                        view.layoutParams = Params.recycler.widthWrap.heightWrap
                    }
                    is LinearLayoutManager -> {
                        if (lm.orientation == LinearLayoutManager.VERTICAL) {
                            view.layoutParams = Params.recycler.widthFill.heightWrap
                        } else {
                            view.layoutParams = Params.recycler.widthWrap.heightFill
                        }
                    }
                }
            }
            return RecyHolder(view)
        }

        override fun onBindViewHolder(holder: RecyHolder, position: Int) {
            val cb = callback
            if (cb != null) {
                cb.onRecyBindView(holder.itemView, position)
            } else {
                val tv = holder.itemView as? TextView ?: return
                tv.text = getItem(position).toString()
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun getItemViewType(position: Int): Int {
            val tc = typeCallback
            if (tc != null) {
                return tc.onRecyItemViewType(position)
            }
            if (itemTypes.isEmpty()) {
                return 0
            }
            return itemTypes.indexOf(getItem(position)::class)
        }

        override fun getItemId(position: Int): Long {
            return callback?.getRecyItemId(position) ?: position.toLong()
        }
    }
}
