@file:Suppress("unused")

package dev.entao.views

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Switch
import kotlin.reflect.KClass


/**
 * Created by yangentao on 2015/11/21.
 * entaoyang@163.com
 */


fun <T : View> KClass<T>.newInstance(context: Context): T {
    val c = this.constructors.first { it.parameters.size == 1 && (it.parameters.first().type.classifier == Context::class || it.parameters.first().type.classifier == Activity::class) }
    return c.call(context)
}


fun <T : View> T.onClick(block: () -> Unit): T {
    this.setOnClickListener {
        block()
    }
    return this
}

fun <T : View> T.onClickView(block: (T) -> Unit): T {
    this.setOnClickListener {
        block(this)
    }
    return this
}

fun <T : View> T.click(block: () -> Unit): T {
    this.setOnClickListener {
        block()
    }
    return this
}

fun <T : View> T.clickView(block: (T) -> Unit): T {
    this.setOnClickListener {
        block(this)
    }
    return this
}


fun View.clickable(b: Boolean = true): View {
    this.isClickable = b
    return this
}

fun <T : Switch> T.onCheckChanged(block: (Switch, Boolean) -> Unit): T {
    this.setOnCheckedChangeListener { view, check ->
        block.invoke(view as Switch, check)
    }
    return this
}