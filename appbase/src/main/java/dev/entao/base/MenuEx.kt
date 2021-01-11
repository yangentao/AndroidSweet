package dev.entao.base

import android.view.Menu
import androidx.annotation.DrawableRes
import dev.entao.appbase.IdGen


fun Menu.buildItems(block: MenuItemBuilder.() -> Unit) {
    val a = MenuItemBuilder(this)
    a.block()
}

class MenuItemBuilder(val menu: Menu) {

    infix fun String.TO(@DrawableRes iconId: Int) {
        menu.add(0, IdGen.gen(), 0, this).setIcon(iconId)
    }

    fun add(title: String) {
        menu.add(title)
    }
}