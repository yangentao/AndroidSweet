@file:Suppress("unused")

package dev.entao.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import dev.entao.appbase.*
import dev.entao.pages.toastShort
import dev.entao.theme.ColorX
import dev.entao.theme.Space
import java.lang.ref.WeakReference
import dev.entao.app.R

/**
 * Created by entaoyang@163.com on 16/3/12.
 */


fun <T : TextView> T.textSize(sp: Int): T {
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp.toFloat())
    return this
}


open class XTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        afterChanged(s.toString())
    }

    open fun afterChanged(text: String) {

    }
}


fun <T : EditText> T.multiLineImeSend(block: (TextView) -> Unit): T {
    gravityTopLeft()
    inputType = InputType.TYPE_CLASS_TEXT
    this.imeOptions = EditorInfo.IME_ACTION_SEND
    this.imeAction(EditorInfo.IME_ACTION_SEND, block)
    maxLines(100)
    setHorizontallyScrolling(false)
    return this
}

fun <T : TextView> T.html(block: HtmlText.() -> Unit): T {
    val h = HtmlText()
    h.block()
    this.text = h.spanned()
    return this
}

@Suppress("DEPRECATION")
fun <T : TextView> T.setHtmlString(s: String) {
    if (Build.VERSION.SDK_INT >= 24) {
        this.text = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)
    } else {
        this.text = Html.fromHtml(s)
    }
}


fun TextView.hideInputMethod() {
    if (this.isFocused) {
        val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(this.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}

fun <T : EditText> T.imeAction(action: Int, block: (TextView) -> Unit): T {

    this.setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == action) {
                if (v != null) {
                    v.hideInputMethod()
                    v.clearFocus()
                    block(v)
                }
                return true
            }
            return false
        }

    })
    return this
}

fun <T : EditText> T.imeDone(): T {
    this.hideInputMethod()
    this.clearFocus()
    this.imeOptions = EditorInfo.IME_ACTION_DONE
    return this
}

fun <T : EditText> T.imeDone(block: (TextView) -> Unit): T {
    this.imeOptions = EditorInfo.IME_ACTION_DONE
    this.imeAction(EditorInfo.IME_ACTION_DONE, block)
    return this
}

fun <T : EditText> T.imeGo(block: (TextView) -> Unit): T {
    this.imeOptions = EditorInfo.IME_ACTION_GO
    this.imeAction(EditorInfo.IME_ACTION_GO, block)
    return this
}

fun <T : EditText> T.imeNext(): T {
    this.imeOptions = EditorInfo.IME_ACTION_NEXT
    return this
}

fun <T : EditText> T.imeSearch(block: (TextView) -> Unit): T {
    this.imeOptions = EditorInfo.IME_ACTION_SEARCH
    this.imeAction(EditorInfo.IME_ACTION_SEARCH, block)
    return this
}

fun <T : EditText> T.imeSend(block: (TextView) -> Unit): T {
    this.imeOptions = EditorInfo.IME_ACTION_SEND
    this.imeAction(EditorInfo.IME_ACTION_SEND, block)
    return this
}


fun <T : TextView> T.gravity(n: Int): T {
    this.gravity = n
    return this
}

fun <T : TextView> T.gravityCenterVertical(): T {
    this.gravity = Gravity.CENTER_VERTICAL
    return this
}

fun <T : TextView> T.gravityCenterHorizontal(): T {
    this.gravity = Gravity.CENTER_HORIZONTAL
    return this
}

@SuppressLint("RtlHardcoded")
fun <T : TextView> T.gravityLeftCenter(): T {
    this.gravity = Gravity.LEFT or Gravity.CENTER
    return this
}

@SuppressLint("RtlHardcoded")
fun <T : TextView> T.gravityRightCenter(): T {
    this.gravity = Gravity.RIGHT or Gravity.CENTER
    return this
}

fun <T : TextView> T.gravityCenter(): T {
    this.gravity = Gravity.CENTER
    return this
}

@SuppressLint("RtlHardcoded")
fun <T : TextView> T.gravityTopLeft(): T {
    this.gravity = Gravity.TOP or Gravity.LEFT
    return this
}

fun <T : TextView> T.miniWidthDp(widthDp: Int): T {
    this.minWidth = widthDp.dp
    return this
}

fun <T : TextView> T.miniHeightDp(heightDp: Int): T {
    this.minHeight = heightDp.dp
    return this
}

fun <T : TextView> T.inputTypePassword(): T {
    this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    return this
}

fun <T : TextView> T.inputTypePasswordNumber(): T {
    this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    return this
}

fun <T : TextView> T.inputTypePhone(): T {
    this.inputType = InputType.TYPE_CLASS_PHONE
    return this
}

fun <T : TextView> T.inputTypeEmail(): T {
    this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    return this
}

fun <T : TextView> T.inputTypeNumber(): T {
    this.inputType = InputType.TYPE_CLASS_NUMBER
    return this
}

fun <T : TextView> T.inputTypeNumberDecimal(): T {
    this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    return this
}


fun <T : TextView> T.lines(lines: Int): T {
    setLines(lines)
    return this
}

fun <T : TextView> T.maxLines(maxLines: Int): T {
    setMaxLines(maxLines)
    return this
}


fun <T : TextView> T.textColor(color: Int): T {
    this.setTextColor(color)
    return this
}


fun <T : TextView> T.textColor(color: Int, pressed: Int): T {
    textColorList(color) {
        lighted(pressed)
    }
    return this
}

fun <T : TextView> T.textColor(ls: ColorStateList): T {
    setTextColor(ls)
    return this
}


fun <T : TextView> T.singleLine(): T {
    this.isSingleLine = true
    return this
}

fun <T : TextView> T.multiLine(): T {
    this.isSingleLine = false
    return this
}

fun <T : TextView> T.ellipsizeStart(): T {
    ellipsize = TextUtils.TruncateAt.START
    return this
}

fun <T : TextView> T.ellipsizeMid(): T {
    ellipsize = TextUtils.TruncateAt.MIDDLE
    return this
}

fun <T : TextView> T.ellipsizeEnd(): T {
    ellipsize = TextUtils.TruncateAt.END
    return this
}

fun <T : TextView> T.ellipsizeMarquee(): T {
    ellipsize = TextUtils.TruncateAt.MARQUEE
    return this
}

fun <T : TextView> T.text(text: String?): T {
    setText(text)
    return this
}

var TextView.textS: String
    get() {
        return this.text.toString()
    }
    set(value) {
        this.text = value
    }

var TextView.textTrim: String
    get() {
        return this.text.toString().trim()
    }
    set(value) {
        this.text = value.trim()
    }


fun <T : TextView> T.textX(text: String?): T {
    setText(text)
    return this
}

fun <T : TextView> T.lineSpace(add: Float, multi: Float): T {
    setLineSpacing(add, multi)
    return this
}

fun <T : TextView> T.fontBold(): T {
    this.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    return this
}

fun <T : TextView> T.hint(text: String): T {
    this.hint = text
    return this
}

fun <T : TextView> T.linkifyAll(): T {
    this.autoLinkMask = Linkify.ALL
    this.movementMethod = LinkMovementMethod.getInstance()
    return this
}

var <T : TextView> T.leftImage: Drawable?
    get() = this.compoundDrawables[0]
    set(value) {
        val old = this.compoundDrawables
        setCompoundDrawables(value, old[1], old[2], old[3])
    }

var <T : TextView> T.topImage: Drawable?
    get() = this.compoundDrawables[1]
    set(value) {
        val old = this.compoundDrawables
        setCompoundDrawables(old[0], value, old[2], old[3])
    }

var <T : TextView> T.rightImage: Drawable?
    get() = this.compoundDrawables[2]
    set(value) {
        val old = this.compoundDrawables
        setCompoundDrawables(old[0], old[1], value, old[3])
    }

var <T : TextView> T.bottomImage: Drawable?
    get() = this.compoundDrawables[3]
    set(value) {
        val old = this.compoundDrawables
        setCompoundDrawables(old[0], old[1], old[2], value)
    }

var <T : TextView> T.imagePadding: Int
    get() = this.compoundDrawablePadding.toDP
    set(value) {
        this.compoundDrawablePadding = value.dp
    }


fun <T : TextView> T.afterChanged(block: (String) -> Unit): T {
    this.addTextChangedListener(object : XTextWatcher() {
        override fun afterChanged(text: String) {
            block(text)
        }

    })
    return this
}

fun <T : TextView> T.afterChangedView(block: (TextView, String) -> Unit): T {
    val tv = this
    this.addTextChangedListener(object : XTextWatcher() {
        override fun afterChanged(text: String) {
            block(tv, text)
        }

    })
    return this
}

fun <T : TextView> T.afterInputChanged(block: (String) -> Unit): T {
    val tv = this
    this.addTextChangedListener(object : XTextWatcher() {
        override fun afterChanged(text: String) {
            if (tv.isInputMethodTarget) {
                block(text)
            }
        }

    })
    return this
}

fun <T : EditText> T.setMaxLength(m: Int) {
    val ed = this
    this.addTextChangedListener(object : XTextWatcher() {
        private var ts: WeakReference<Toast>? = null
        override fun afterTextChanged(s: Editable) {
            if (s.length > m) {
                ts?.get()?.cancel()
                val t = ed.context.toastShort("最多 $m 个字符")
                ts = WeakReference(t)
                s.delete(ed.selectionStart - 1, ed.selectionEnd)
                ed.setTextKeepState(s)
            }
        }
    })
}


private fun calcClearButtonSize(ed: EditText): Int {
    if (ed.height > 20) {
        return px2dp(ed.height) * 3 / 4
    }
    return 25
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.withClearButton(clearColor: Int = ColorX.redDark) {
    this.setOnTouchListener { _, event ->
        onTouchEdit(this, event)
    }
    val ed = this

    this.addTextChangedListener(object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val sz = calcClearButtonSize(ed)

            val d = if (ed.text.toString() == "")
                null
            else
                R.mipmap.yet_edit_clear.resDrawable.sized(sz).tinted(clearColor)
            ed.setCompoundDrawables(null, null, d, null)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun afterTextChanged(s: Editable) {

        }
    })
}

private fun onTouchEdit(ed: EditText, event: MotionEvent): Boolean {
    if (ed.compoundDrawables[2] == null) {
        return false
    }
    if (event.action != MotionEvent.ACTION_UP) {
        return false
    }
    val sz = calcClearButtonSize(ed)
    if (event.x > ed.width - ed.paddingRight - sz - 15.dp) {
        ed.setText("")
        ed.setCompoundDrawables(null, null, null, null)
        Task.fore {
            ed.hideInputMethod()
            ed.clearFocus()
        }
    }
    return false
}

fun TextView.moreArrow() {
    rightImage = R.mipmap.yet_arrow_right.resDrawable.mutate().sized(12)
    imagePadding = Space.X0
}
