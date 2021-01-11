package dev.entao.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import dev.entao.appbase.*
import dev.entao.theme.ColorX
import kotlin.reflect.KClass


interface GridCallback : AbsListCallback {
    override fun onNewView(context: Context, position: Int): View {
        return AppCompatTextView(context).apply {
            styleGridItemView()
            layoutParams = Params.list.widthFill.heightWrap
        }
    }

}

fun ViewGroup.gridViewX(block: GridViewX.() -> Unit): GridViewX {
    return append {
        this.block()
    }
}

open class GridViewX(context: Context) : BaseGridView(context) {

    protected val items = ArrayList<Any>(128)
    private val itemsBack = ArrayList<Any>()
    private var types: List<KClass<*>> = emptyList()
    var typeCallback: AdapterTypeCallback? = null
    var callback: GridCallback? = null
    var clickCallback: AdapterClickCallback? = null
    private val itemClickListener = OnItemClickListener { _, view, position, _ -> onItemClick(view, position) }

    init {
        this.adapter = MyAdapter()
        onItemClickListener = itemClickListener
        this.numColumns = 3
        this.gravity = Gravity.CENTER
        this.cacheColorHint = 0
    }

    protected open fun onItemClick(view: View, position: Int) {
        clickCallback?.onClickAdapter(position, getItem(position))
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
            styleGridItemView()
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

}

open class BaseGridView(context: Context) : GridView(context) {

    //dp
    var preferColumnWidth: Int = 64
    var autoColumn = false
    var heightMost = false

    var enableLine = false
    var lineColor = ColorX.divider
    var lineWidth = 1
    private val localPaint = Paint()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            val newWidth = r - l
            if (autoColumn && preferColumnWidth > 0) {
                val ww = newWidth - this.paddingLeft - this.paddingRight
                var cols = (ww + this.horizontalSpacing) / (preferColumnWidth.dp + this.horizontalSpacing)
                if (cols < 1) {
                    cols = 1
                }
                Task.fore {
                    this.numColumns = cols
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!heightMost) {
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        val heightSpec: Int = if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
            MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        } else {
            heightMeasureSpec
        }

        super.onMeasure(widthMeasureSpec, heightSpec)
    }


    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (!enableLine) {
            return
        }
        val childCount = this.childCount
        if (childCount <= 0) {
            return
        }
        localPaint.style = Paint.Style.STROKE //画笔实心
        localPaint.color = lineColor//画笔颜色
        localPaint.strokeWidth = lineWidth.toFloat()
        val colCount = this.numColumns
        for (i in 0 until childCount) {
            val cellView = getChildAt(i)
            //画item下边分割线
            canvas.drawLine(cellView.left.toFloat(), cellView.bottom.toFloat(), cellView.right.toFloat(), cellView.bottom.toFloat(), localPaint)
            //画item右边分割线
            if ((i + 1) % colCount != 0) {
                canvas.drawLine(cellView.right.toFloat(), cellView.top.toFloat(), cellView.right.toFloat(), cellView.bottom.toFloat(), localPaint)
            }
        }
    }
}