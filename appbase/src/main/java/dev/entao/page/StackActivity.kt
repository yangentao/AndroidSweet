package dev.entao.page

import android.os.Bundle
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import dev.entao.pages.toast
import dev.entao.views.FrameLayout


typealias LifeState = Lifecycle.State
typealias LifeEvent = Lifecycle.Event

open class StackActivity : BaseActivity() {

    lateinit var pageContainer: StackContainer
    lateinit var containerFrameLayout: FrameLayout
        private set

    var doubleBack = false
    private var lastBackTime: Long = 0

    open fun getInitPage(): Page? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        containerFrameLayout = FrameLayout {
            fitsSystemWindows = true
        }
        setContentView(containerFrameLayout)
        pageContainer = StackContainer(this, containerFrameLayout)
        val p = getInitPage()
        if (p != null) {
            pageContainer.pushPage(p)
        }
    }

    fun setContentPage(p: Page) {
        pageContainer.setContentPage(p)
    }

    fun <T : Page> setContentPage(p: T, block: T.() -> Unit) {
        p.block()
        pageContainer.setContentPage(p)
    }

    fun pushPage(p: Page) {
        pageContainer.pushPage(p)
    }

    fun <T : Page> pushPage(p: T, block: T.() -> Unit) {
        p.block()
        pageContainer.pushPage(p)
    }

    override fun onBackPressed() {
        if (pageContainer.topPage?.onBackPressed() == true) {
            return
        }
        if (pageContainer.pageCount > 1) {
            pageContainer.popPage()
            return
        }
        if (allowFinish()) {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (pageContainer.topPage?.onKeyDown(keyCode, event) == true) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (pageContainer.topPage?.onKeyUp(keyCode, event) == true) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }


    protected open fun allowFinish(): Boolean {
        if (doubleBack) {
            val cur = System.currentTimeMillis()
            if (cur - lastBackTime < 2000) {
                return true
            }
            lastBackTime = cur
            toast("再按一次返回键退出")
            return false
        } else {
            return true
        }
    }


}

