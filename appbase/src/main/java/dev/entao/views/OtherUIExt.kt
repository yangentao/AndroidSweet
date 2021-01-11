package dev.entao.views

import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.PopupWindow

/**
 * Created by entaoyang@163.com on 2018-04-18.
 */

fun PopupWindow.showDropUp(anchor: View, xoff: Int, yoff: Int) {
    val v = this.contentView
    v.measure(makeSpec(this.width), makeSpec(this.height))
    val h = v.measuredHeight + anchor.height
    this.showAsDropDown(anchor, xoff, -h + yoff)
}

private fun makeSpec(measureSpec: Int): Int {
    val mode: Int = if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
        View.MeasureSpec.UNSPECIFIED
    } else {
        View.MeasureSpec.EXACTLY
    }
    return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
}

fun ListView.scrollToBottom() {
    val c = this.adapter?.count ?: return
    if (c > 0) {
        this.setSelection(c - 1)
    }
}