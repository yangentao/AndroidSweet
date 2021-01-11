@file:Suppress("unused")

package dev.entao.views

import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

typealias WindowParams = WindowManager.LayoutParams
typealias GroupParams = ViewGroup.LayoutParams
typealias MarginParams = ViewGroup.MarginLayoutParams
typealias LinearParams = LinearLayout.LayoutParams
typealias RelativeParams = RelativeLayout.LayoutParams
typealias FrameParams = FrameLayout.LayoutParams
typealias ListParams = AbsListView.LayoutParams
typealias PagerParams = ViewPager.LayoutParams
typealias DrawerParams = DrawerLayout.LayoutParams
typealias GridParams = GridLayout.LayoutParams
typealias TableParams = TableLayout.LayoutParams
typealias RowParams = TableRow.LayoutParams
typealias ConstraintParams = ConstraintLayout.LayoutParams
typealias RecyParams = RecyclerView.LayoutParams

object Params {
    val window: WindowParams get() = WindowParams(GroupParams.MATCH_PARENT, GroupParams.WRAP_CONTENT)
    val group: GroupParams get() = GroupParams(GroupParams.WRAP_CONTENT, GroupParams.WRAP_CONTENT)
    val margin: MarginParams get() = MarginParams(GroupParams.WRAP_CONTENT, GroupParams.WRAP_CONTENT)
    val linear: LinearParams get() = LinearParams(GroupParams.WRAP_CONTENT, GroupParams.WRAP_CONTENT)
    val relative: RelativeParams get() = RelativeParams(GroupParams.WRAP_CONTENT, GroupParams.WRAP_CONTENT)
    val frame: FrameParams get() = FrameParams(GroupParams.WRAP_CONTENT, GroupParams.WRAP_CONTENT)
    val list: ListParams get() = ListParams(GroupParams.MATCH_PARENT, GroupParams.WRAP_CONTENT)
    val grid: GridParams get() = GridParams()
    val pager: PagerParams get() = PagerParams()
    val drawer: DrawerParams get() = DrawerParams(GroupParams.MATCH_PARENT, GroupParams.WRAP_CONTENT)
    val table: TableParams get() = TableParams(TableParams.MATCH_PARENT, TableParams.WRAP_CONTENT)
    val row: RowParams get() = RowParams(RowParams.MATCH_PARENT, RowParams.WRAP_CONTENT)
    val constraint: ConstraintParams get() = ConstraintParams(ConstraintParams.MATCH_CONSTRAINT, ConstraintParams.MATCH_CONSTRAINT)
    val recycler: RecyParams get() = RecyParams(RecyParams.MATCH_PARENT, RecyParams.WRAP_CONTENT)

}

fun View.groupParams(block: GroupParams.() -> Unit) {
    this.layoutParams = Params.group.apply(block)
}

fun View.linearParams(block: LinearParams.() -> Unit) {
    this.layoutParams = Params.linear.apply(block)
}

fun View.marginParams(block: MarginParams.() -> Unit) {
    this.layoutParams = Params.margin.apply(block)
}

fun View.relativeParams(block: RelativeParams.() -> Unit) {
    this.layoutParams = Params.relative.apply(block)
}


fun View.frameParams(block: FrameParams.() -> Unit) {
    this.layoutParams = Params.frame.apply(block)
}

fun View.listParams(block: ListParams.() -> Unit) {
    this.layoutParams = Params.list.apply(block)
}

fun View.gridParams(block: GridParams.() -> Unit) {
    this.layoutParams = Params.grid.apply(block)
}

fun View.pagerParams(block: PagerParams.() -> Unit) {
    this.layoutParams = Params.pager.apply(block)
}

fun View.drawerParams(block: DrawerParams.() -> Unit) {
    this.layoutParams = Params.drawer.apply(block)
}

fun View.recyclerParams(block: RecyParams.() -> Unit) {
    this.layoutParams = Params.recycler.apply(block)
}

fun View.constraintParams(block: ConstraintParams.() -> Unit) {
    if (this is Guideline) {
        this.layoutParams = Params.constraint.wrap
    } else {
        this.layoutParams = Params.constraint.apply(block)
    }
}


