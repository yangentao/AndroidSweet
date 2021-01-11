@file:Suppress("unused", "FunctionName")

package dev.entao.views


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton

inline fun <reified T : View> ViewGroup.addViewX(child: T): T {
    this.addView(child)
    return child
}

inline fun <reified T : View> ViewGroup.addViewX(child: T, param: GroupParams): T {
    this.addView(child, param)
    return child
}

inline fun <reified T : View> ViewGroup.addViewX(child: T, index: Int, param: GroupParams): T {
    this.addView(child, index, param)
    return child
}


inline fun <reified T : View> ViewGroup.addViewX(child: T, params: GroupParams, block: T.() -> Unit): T {
    this.addView(child, params)
    child.block()
    return child
}

inline fun <reified T : View> LinearLayout.addViewX(child: T, block: LinearParams.() -> Unit): T {
    val p = Params.linear
    p.block()
    this.addView(child, p)
    return child
}

inline fun <reified T : View> ViewGroup.append(block: T.() -> Unit): T {
    val b = T::class.newInstance(this.context).needId()
    this.addView(b)
    b.block()
    return b
}


inline fun <reified T : View> ViewGroup.append(params: ViewGroup.LayoutParams, block: T.() -> Unit): T {
    val b = T::class.newInstance(this.context).needId()
    this.addView(b, params)
    b.block()
    return b
}

//=====Activity====

var Activity.ContentView: View
    get() {
        return this.findViewById<ViewGroup>(android.R.id.content).child(0)!!
    }
    set(value) = this.setContentView(value)


fun Activity.LinearLayout(block: LinearLayout.() -> Unit): LinearLayout {
    val b = LinearLayout(this).needId()
    b.block()
    return b
}

fun Activity.LinearLayoutH(block: LinearLayout.() -> Unit): LinearLayout {
    val b = LinearLayout(this).needId()
    b.horizontal()
    b.block()
    return b
}

fun Activity.LinearLayoutV(block: LinearLayout.() -> Unit): LinearLayout {
    val b = LinearLayout(this).needId()
    b.vertical()
    b.block()
    return b
}

fun Activity.RelativeLayout(block: RelativeLayout.() -> Unit): RelativeLayout {
    val b = RelativeLayout(this).needId()
    b.block()
    return b
}

fun Activity.FrameLayout(block: FrameLayout.() -> Unit): FrameLayout {
    val b = FrameLayout(this).needId()
    b.block()
    return b
}

//=======Fragment======
fun Fragment.LinearLayout(block: LinearLayout.() -> Unit): LinearLayout {
    val b = LinearLayout(this.requireActivity()).needId()
    b.block()
    return b
}

fun Fragment.LinearLayoutH(block: LinearLayout.() -> Unit): LinearLayout {
    val b = LinearLayout(this.requireActivity()).needId()
    b.horizontal()
    b.block()
    return b
}

fun Fragment.LinearLayoutV(block: LinearLayout.() -> Unit): LinearLayout {
    val b = LinearLayout(this.requireActivity()).needId()
    b.vertical()
    b.block()
    return b
}

fun Fragment.RelativeLayout(block: RelativeLayout.() -> Unit): RelativeLayout {
    val b = RelativeLayout(this.requireActivity()).needId()
    b.block()
    return b
}

fun Fragment.FrameLayout(block: FrameLayout.() -> Unit): FrameLayout {
    val b = FrameLayout(this.requireActivity()).needId()
    b.block()
    return b
}


//=======ViewGroup==========

fun ViewGroup.linearLayout(block: LinearLayout.() -> Unit): LinearLayout {
    return append(block)
}

fun ViewGroup.linearLayout(params: GroupParams, block: LinearLayout.() -> Unit): LinearLayout {
    return append(params, block)
}

fun ViewGroup.linearLayoutH(block: LinearLayout.() -> Unit): LinearLayout {
    return append {
        horizontal()
        this.block()
    }
}

fun ViewGroup.linearLayoutH(params: GroupParams, block: LinearLayout.() -> Unit): LinearLayout {
    return append(params) {
        horizontal()
        this.block()
    }
}

fun ViewGroup.linearLayoutV(block: LinearLayout.() -> Unit): LinearLayout {
    return append {
        vertical()
        this.block()
    }
}

fun ViewGroup.linearLayoutV(params: GroupParams, block: LinearLayout.() -> Unit): LinearLayout {
    return append(params) {
        vertical()
        this.block()
    }
}

fun ViewGroup.relativeLayout(block: RelativeLayout.() -> Unit): RelativeLayout {
    return append(block)
}

fun ViewGroup.relativeLayout(
    params: GroupParams,
    block: RelativeLayout.() -> Unit
): RelativeLayout {
    return append(params, block)
}

fun ViewGroup.frameLayout(block: FrameLayout.() -> Unit): FrameLayout {
    return append(block)
}

fun ViewGroup.frameLayout(params: GroupParams, block: FrameLayout.() -> Unit): FrameLayout {
    return append(params, block)
}

fun ViewGroup.drawerLayout(block: DrawerLayout.() -> Unit): DrawerLayout {
    return append(block)
}

fun ViewGroup.drawerLayout(params: GroupParams, block: DrawerLayout.() -> Unit): DrawerLayout {
    return append(params, block)
}

fun ViewGroup.viewPager(block: ViewPager.() -> Unit): ViewPager {
    return append(block)
}

fun ViewGroup.viewPager(params: GroupParams, block: ViewPager.() -> Unit): ViewPager {
    return append(params, block)
}

//fun ViewGroup.viewPager2(block: ViewPager2.() -> Unit): ViewPager2 {
//    return append(block)
//}
fun ViewGroup.textView(block: TextView.() -> Unit): TextView {
    return append(block)
}


fun ViewGroup.textView(params: GroupParams, block: TextView.() -> Unit): TextView {
    return append(params, block)
}

fun ViewGroup.button(block: Button.() -> Unit): Button {
    return append {
        this.styleDefault()
        this.block()
    }
}


fun ViewGroup.button(params: ViewGroup.LayoutParams, block: Button.() -> Unit): Button {
    return append(params) {
        this.styleDefault()
        this.block()
    }
}

fun ViewGroup.checkBox(block: CheckBox.() -> Unit): CheckBox {
    return append(block)
}

fun ViewGroup.checkBox(params: ViewGroup.LayoutParams, block: CheckBox.() -> Unit): CheckBox {
    return append(params, block)
}


fun ViewGroup.editText(block: EditText.() -> Unit): EditText {
    return append(block)
}

fun ViewGroup.editText(params: ViewGroup.LayoutParams, block: EditText.() -> Unit): EditText {
    return append(params, block)
}

fun ViewGroup.gridView(block: GridView.() -> Unit): GridView {
    return append(block)
}

fun ViewGroup.gridView(params: ViewGroup.LayoutParams, block: GridView.() -> Unit): GridView {
    return append(params, block)
}

fun ViewGroup.imageView(block: ImageView.() -> Unit): ImageView {
    return append {
        this.styleDefault()
        this.block()
    }
}

fun ViewGroup.imageView(params: ViewGroup.LayoutParams, block: ImageView.() -> Unit): ImageView {
    return append(params) {
        this.styleDefault()
        this.block()
    }
}

fun ViewGroup.imageButton(block: ImageButton.() -> Unit): ImageButton {
    return append(block)
}

fun ViewGroup.imageButton(
    params: ViewGroup.LayoutParams,
    block: ImageButton.() -> Unit
): ImageButton {
    return append(params, block)
}

fun ViewGroup.listView(block: ListView.() -> Unit): ListView {
    return append {
        this.styleDeftault()
        this.block()
    }
}

fun ViewGroup.listView(params: ViewGroup.LayoutParams, block: ListView.() -> Unit): ListView {
    return append(params) {
        this.styleDeftault()
        this.block()
    }
}

fun RadioGroup.radioButton(block: RadioButton.() -> Unit): RadioButton {
    return append(block)
}

fun RadioGroup.radioButtonMeterial(block: MaterialRadioButton.() -> Unit): MaterialRadioButton {
    return append(block)
}


fun ViewGroup.radioGroup(block: RadioGroup.() -> Unit): RadioGroup {
    return append(block)
}

fun ViewGroup.radioGroup(params: ViewGroup.LayoutParams, block: RadioGroup.() -> Unit): RadioGroup {
    return append(params, block)
}

fun RadioGroup.checkButtonMeterial(block: MaterialCheckBox.() -> Unit): MaterialCheckBox {
    return append(block)
}


fun ViewGroup.tableLayout(block: TableLayout.() -> Unit): TableLayout {
    return append(block)
}

fun ViewGroup.tableLayout(
    params: ViewGroup.LayoutParams,
    block: TableLayout.() -> Unit
): TableLayout {
    return append(params, block)
}

fun ViewGroup.tableRow(block: TableRow.() -> Unit): TableRow {
    return append(block)
}

fun ViewGroup.tableRow(params: ViewGroup.LayoutParams, block: TableRow.() -> Unit): TableRow {
    return append(params, block)
}

fun ViewGroup.scrollView(block: ScrollView.() -> Unit): ScrollView {
    return append(block)
}

fun ViewGroup.scrollView(params: ViewGroup.LayoutParams, block: ScrollView.() -> Unit): ScrollView {
    return append(params, block)
}

fun ViewGroup.scrollViewH(block: HorizontalScrollView.() -> Unit): HorizontalScrollView {
    return append(block)
}

fun ViewGroup.scrollViewH(
    params: ViewGroup.LayoutParams,
    block: HorizontalScrollView.() -> Unit
): HorizontalScrollView {
    return append(params, block)
}

fun ViewGroup.view(block: View.() -> Unit): View {
    return append(block)
}

fun ViewGroup.view(params: ViewGroup.LayoutParams, block: View.() -> Unit): View {
    return append(params, block)
}

fun ViewGroup.constraint(block: ConstraintLayout.() -> Unit): ConstraintLayout {
    return append(block)
}

fun ViewGroup.gridLayout(block: GridLayout.() -> Unit): GridLayout {
    return append(block)
}