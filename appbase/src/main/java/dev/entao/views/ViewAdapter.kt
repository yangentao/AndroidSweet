package dev.entao.views

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class ViewAdapter<T : View> : BaseAdapter() {
    var items = ArrayList<T>()
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return items[position]
    }
}