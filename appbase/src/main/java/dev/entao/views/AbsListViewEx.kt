package dev.entao.views

import android.content.Context
import android.view.View
import android.widget.TextView

interface AdapterTypeCallback {
    fun onAdapterTypeCount(): Int
    fun onAdapterViewType(position: Int, item: Any): Int
}


interface AbsListCallback {
    fun onNewView(context: Context, position: Int): View

    fun onBindView(itemView: View, item: Any) {
        if (itemView !is TextView) return
        itemView.text = item.toString()
    }

    fun onItemsChanged() {}
    fun onItemId(position: Int): Long {
        return position.toLong()
    }


}

interface AdapterClickCallback {
    fun onClickAdapter(position: Int, item: Any) {}
}

interface ItemCheckCallback {

    fun onItemKey(item: Any): String {
        return item.toString()
    }

    fun onItemCheckable(item: Any): Boolean {
        return true
    }

    fun onCheckChanged(item: Any) {}
}
