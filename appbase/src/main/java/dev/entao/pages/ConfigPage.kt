@file:Suppress("MemberVisibilityCanBePrivate")

package dev.entao.pages

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import dev.entao.appbase.*
import dev.entao.page.LinearPage
import dev.entao.views.*
import dev.entao.theme.ColorX
import dev.entao.theme.Space
import dev.entao.theme.heightButton

open class ConfigPage : LinearPage() {
    val items: ArrayList<ConfigItem> = ArrayList()

    init {
        enableContentScroll = true
    }


    override fun onPageCreated() {
        super.onPageCreated()
        for (item in items) {
            if (item.itemView.hasOnClickListeners()) {
                item.itemView.background = ColorX.itemFadeBackground
//                item.itemView.backColor(ColorX.pageBackground, ColorX.fade)
            }
        }
    }


    fun findItem(key: String): ConfigItem {
        return items.first { it.key == key }
    }

    fun removeItem(key: String): ConfigItem {
        val item = items.first { it.key == key }
        item.itemView.removeFromParent()
        return item
    }

    fun <T : ConfigItem> addItem(item: T): T {
        items += item
        val v = item.itemView
        if (v.layoutParams !is LinearParams) {
            v.layoutParams = Params.linear.widthFill.heightWrap
        }
        if (v is ViewGroup) {
            v.padding(20, 12, 20, 12)
        }

        contentView.addView(v)
        return item
    }


    fun line(): LineConfigItem {
        val item = LineConfigItem(context)
        return addItem(item)
    }

    fun groupLine(title: String): GroupLineConfigItem {
        val a = GroupLineConfigItem(context)
        a.title = title
        return addItem(a)
    }


    fun labelValue(block: LabelValueConfigItem.() -> Unit) {
        val a = LabelValueConfigItem(context)
        a.block()
        addItem(a)
    }

    fun <T : Any> labelValueX(block: LabelValueConfigItemX<T>.() -> Unit) {
        val a = LabelValueConfigItemX<T>(context)
        a.block()
        addItem(a)
    }

    fun labelSwitch(block: LabelSwitchItem.() -> Unit): LabelSwitchItem {
        val a = LabelSwitchItem(context)
        a.block()
        return addItem(a)
    }

    fun button(label: String, block: ButtonConfigItem.() -> Unit) {
        val a = ButtonConfigItem(context)
        a.itemView.text = label
        a.block()
        addItem(a)
    }
}


abstract class ConfigItem(val context: Context) {
    var key: String = "key" + IdGen.gen()
    abstract val itemView: View


}

fun <T : ConfigItem> T.onItemClick(block: (T) -> Unit) {
    itemView.onClick {
        block(this)
    }
}

class ButtonConfigItem(context: Context) : ConfigItem(context) {
    override val itemView: Button

    init {
        itemView = Button(context).apply {
            linearParams {
                gravityCenter.heightButton.width(200).marginY(30, 10)
            }
            val c = StateList.color(ColorX.textPrimary) {
                disabled(ColorX.textDisabled)
                pressed(Color.WHITE)
            }
            this.setTextColor(c)
            this.elevation = 20.dpf

            this.backgroundTintList = StateList.color(0xeeeeee.rgb) {
                disabled(0xcccccc.rgb)
                pressed(ColorX.fade)
            }
//            styleGreenRound()
        }

    }
}

open class LabelItem(context: Context) : ConfigItem(context) {
    final override val itemView: LinearLayout = LinearLayout(context).horizontal()
    lateinit var labelView: TextView

    init {
        itemView.textView {
            linearParams {
                gravityCenterY.heightWrap.flexX
            }
            stylePrimaryText()
            labelView = this
        }
    }

    var label: String
        get() = labelView.textS
        set(value) {
            labelView.textS = value
        }


}

fun <T : LabelItem> T.icon(resId: Int): T {
    labelView.imagePadding = Space.X0
    labelView.leftImage = resId.resDrawable.mutate().sized(24)
    return this
}

class LabelSwitchItem(context: Context) : LabelItem(context) {
    val switchButton: SwitchMaterial = itemView.switchMeterial {
        linearParams {
            wrap.gravityRightCenter
        }
    }


    var value: Boolean
        get() = switchButton.isChecked
        set(value) {
            switchButton.isChecked = value
        }

    fun onValueChanged(block: (LabelSwitchItem) -> Unit) {
        switchButton.setOnCheckedChangeListener { _, _ ->
            block(this)
        }
    }


}

open class LabelValueConfigItem(context: Context) : LabelItem(context) {
    lateinit var valueView: TextView
    var onValueChanged: (LabelValueConfigItem) -> Unit = {}

    init {
        itemView.textView {
            linearParams {
                heightWrap.widthWrap.gravityCenterY
            }
            styleSecondaryText()
            gravityRightCenter()
            valueView = this
        }

    }

    fun arrow(): LabelValueConfigItem {
        valueView.moreArrow()
        return this
    }

    var value: String
        get() = valueView.textS
        set(value) {
            valueView.textS = value
        }

    fun optionItems(vararg ls: String) {
        optionList(ls.toList())
    }

    fun optionList(ls: List<String>) {
        onItemClick {
            XDialog(context).showListString(null, ls) { _, s ->
                value = s
                onValueChanged(this)
            }

        }
    }

    //checkBlock 返回null表示验证通过, 非null则提示错误
    fun input(title: String, config: InputConfig) {
        onItemClick {

            XDialog(context).showInput(title, value, { config.onEditConfig(it) }) {
                val p = config.onInputValue(it)
                if (p.first) {
                    value = p.second
                    onValueChanged(this)
                } else {
                    context.toast(p.second)
                }
            }

        }
    }
}

interface InputConfig {
    fun onEditConfig(ed: EditText) {}
    fun onInputValue(inputText: String): Pair<Boolean, String> {
        return true to inputText
    }
}

interface InputConfigX<T : Any> {
    fun onConfigEdit(ed: EditText) {}
    fun onCheck(inputText: String): String? {
        return null
    }

    fun valueToText(v: Any?): String {
        return v?.toString() ?: ""
    }

    fun textToValue(inputText: String): T?
}

class LabelValueConfigItemX<T : Any>(context: Context) : LabelItem(context) {
    lateinit var valueView: TextView
    var onValueChanged: (LabelValueConfigItemX<T>) -> Unit = {}

    var onDisplay: (T?) -> String = ::displayDefault

    init {
        itemView.textView {
            linearParams {
                heightWrap.widthWrap.gravityCenterY
            }
            styleSecondaryText()
            gravityRightCenter()
            valueView = this
        }

    }

    fun displayDefault(v: T?): String {
        return v?.toString() ?: ""
    }

    fun displayByPairs(value: T?, ls: List<Pair<String, T>>): String {
        if (value != null) {
            for (p in ls) {
                if (p.second == value) {
                    return p.first
                }
            }
        }
        return ""
    }

    fun arrow(): LabelValueConfigItemX<T> {
        valueView.moreArrow()
        return this
    }

    var value: T? = null
        set(value) {
            field = value
            valueView.text = onDisplay(value)
        }

    fun optionPairs(vararg ps: Pair<String, T>) {
        optionList(ps.toList())
    }

    fun optionMap(map: Map<String, T>) {
        optionList(map.map { it.key to it.value })
    }


    fun optionList(ls: List<Pair<String, T>>) {
        onDisplay = {
            displayByPairs(it, ls)
        }
        onItemClick {
            XDialog(context).showListItems(null, ls, { it.first }) { _, item ->
                value = item.second
                onValueChanged(this)
            }

        }
    }


    fun input(title: String, config: InputConfigX<T>) {
        onDisplay = {
            config.valueToText(it)
        }
        onItemClick { _ ->
            XDialog(context).showInput(title, if (value == null) "" else config.valueToText(value), { config.onConfigEdit(it) }) { s ->
                val err = config.onCheck(s)
                if (err == null) {
                    value = config.textToValue(s)
                    onValueChanged(this)
                } else {
                    context.toast(err)
                }
            }

        }
    }


}

//abstract class ClassGenericTypeTake<T> {
//
//    val type: KType by lazy { this::class.supertypes.first().arguments.first().type!! }
//    fun typeParam(): Type {
//        return (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.first()
//    }
//}
//
//fun EditText.inputTypeByProp(p: Prop) {
//
//    logd("return type : ", p.returnType, p.firstGenericType)
//    if (p.isTypeInt || p.isTypeLong) {
//        inputTypeNumber()
//    } else if (p.isTypeFloat || p.isTypeDouble) {
//        inputTypeNumberDecimal()
//    }
//}

open class LineConfigItem(context: Context) : ConfigItem(context) {
    final override val itemView: LinearLayout = LinearLayout(context).vertical()
    lateinit var lineView: View

    init {
        itemView.apply {
            view {
                linearParams {
                    widthFill.height(1)
                }
                backColor(ColorX.divider)
                lineView = this
            }
        }
    }
}

class GroupLineConfigItem(context: Context) : LineConfigItem(context) {
    lateinit var titleView: TextView

    init {
        itemView.textView {
            linearParams {
                wrap.marginTop(10)
            }
            styleSecondaryText()
            singleLine()
            titleView = this
        }
    }

    var title: String
        get() = titleView.textS
        set(value) {
            titleView.textS = value
        }

    fun hideLine() {
        lineView.gone()
    }

}


