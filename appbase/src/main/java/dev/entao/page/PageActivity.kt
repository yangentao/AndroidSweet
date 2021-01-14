package dev.entao.page

import android.os.Bundle
import android.view.KeyEvent
import android.widget.FrameLayout
import dev.entao.pages.toast
import dev.entao.views.FrameLayout

open class PageActivity : BaseActivity() {

    lateinit var pageManager: StackPageContainer
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
        pageManager = StackPageContainer(this, containerFrameLayout)
        val p = getInitPage()
        if (p != null) {
            pageManager.pushPage(p)
        }
    }

    fun setContentPage(p: Page) {
        pageManager.setContentPage(p)
    }

    fun <T : Page> setContentPage(p: T, block: T.() -> Unit) {
        p.block()
        pageManager.setContentPage(p)
    }

    fun pushPage(p: Page) {
        pageManager.pushPage(p)
    }

    fun <T : Page> pushPage(p: T, block: T.() -> Unit) {
        p.block()
        pageManager.pushPage(p)
    }

    override fun onBackPressed() {
        if (pageManager.topPage?.onBackPressed() == true) {
            return
        }
        if (pageManager.pageCount > 1) {
            pageManager.popPage()
            return
        }
        if (allowFinish()) {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (pageManager.topPage?.onKeyDown(keyCode, event) == true) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (pageManager.topPage?.onKeyUp(keyCode, event) == true) {
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
