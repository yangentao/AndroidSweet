package dev.entao.pages

import android.content.Context
import android.view.View
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.TextView
import dev.entao.appbase.grayColor
import dev.entao.views.*
import kotlin.reflect.KClass

class GroupItem<T : Any> {
    var label: String = ""
    var items: ArrayList<T> = ArrayList()
}

class GroupData<T : Any> {
    var flatList: ArrayList<Any> = ArrayList()
    var groupList: ArrayList<GroupItem<T>> = ArrayList()

    var labelOf: (T) -> String = { throw IllegalAccessException("重写此方法") }
    var itemsSorter: (ArrayList<T>) -> Unit = { throw IllegalAccessException("重写此方法") }

    var groupSorter: (ArrayList<GroupItem<T>>) -> Unit = { ls ->
        ls.sortBy { it.label }
    }

    val labelList: List<String>
        get() {
            return this.groupList.map { it.label }
        }


    fun process(items: List<T>) {
        val gl = ArrayList<GroupItem<T>>()
        val map = HashMap<String, GroupItem<T>>()
        for (item in items) {
            val lb = labelOf(item)
            val g = map[lb]
            val gg = if (g == null) {
                val a = GroupItem<T>()
                map[lb] = a
                gl.add(a)
                a
            } else {
                g
            }
            gg.label = lb
            gg.items.add(item)
        }
        for (g in gl) {
            this.itemsSorter(g.items)
        }
        this.groupSorter(gl)
        val ls = ArrayList<Any>(items.size + map.size)
        for (a in gl) {
            ls.add(a)
            ls.addAll(a.items)
        }
        this.flatList = ls
        this.groupList = gl
    }
}

abstract class GroupPage<T : Any>(val itemClass: KClass<T>) : ListPage() {
    private val groupData: GroupData<T> = GroupData()
    private lateinit var groupIndexBar: GroupIndexBar

    init {
        groupData.labelOf = {
            this.labelOfItem(it)
        }
        groupData.itemsSorter = { ls ->
            this.onSortItems(ls)

        }
        groupData.groupSorter = {
            this.onSortGroups(it)
        }
    }

    abstract fun labelOfItem(item: T): String
    abstract fun onSortItems(ls: ArrayList<T>)

    abstract fun onNewItemView(context: Context, position: Int): View
    abstract fun onBindItemView(itemView: View, item: T)
    abstract fun onRequestItemModels(): List<T>


    open fun onSortGroups(gs: ArrayList<GroupItem<T>>) {
        gs.sortBy { it.label }
    }


    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)
        listView.itemTypes(GroupItem::class, itemClass)
        groupIndexBar = GroupIndexBar(context)
        this.listViewParent.addView(this.groupIndexBar, Params.relative.width(GroupIndexBar.WIDTH_PREFER).parentRight.parentTop.parentBottom)
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            @Suppress("UNCHECKED_CAST")
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (visibleItemCount > 0) {
                    val obj = getItem(firstVisibleItem)
                    if (obj::class == itemClass) {
                        val lb = labelOfItem(obj as T)
                        groupIndexBar.setCurrentLabel(lb)
                    } else if (obj is GroupItem<*>) {
                        groupIndexBar.setCurrentLabel(obj.label)
                    }
                }
            }
        })

        this.groupIndexBar.onLabelChanged = this::labelChanged
    }

    private fun labelChanged(lb: String) {
        var n = this.listView.headerViewsCount
        for (g in this.groupData.groupList) {
            if (g.label == lb) {
                this.listView.setSelection(n)
                return
            }
            n += 1 + g.items.size
        }
    }

    override fun onItemsChanged() {
        super.onItemsChanged()
        if (listView.inFilter) {
            this.groupIndexBar.gone()
        } else {
            this.groupIndexBar.visiable()
            this.groupIndexBar.setLabelItems(this.groupData.labelList)
        }
    }


    final override fun onNewView(context: Context, position: Int): View {
        val item = getItem(position)
        return if (item::class == GroupItem::class) {
            TextView(context).apply {
                padding(10, 0, 0, 0)
                backColor(grayColor(0xbb))
                textColorWhite()
                textSizePrimary()
                gravityLeftCenter()
                layoutParams = Params.margin.widthFill.height(20)
            }
        } else {
            onNewItemView(context, position)
        }
    }

    @Suppress("UNCHECKED_CAST")
    final override fun onBindView(itemView: View, item: Any) {
        if (item is GroupItem<*>) {
            val v = itemView as TextView
            v.text = item.label
        } else {
            onBindItemView(itemView, item as T)
        }
    }


    final override fun onRequestItems(args: Any?): List<Any> {
        val ls = onRequestItemModels()
        groupData.process(ls)
        return groupData.flatList
    }
}