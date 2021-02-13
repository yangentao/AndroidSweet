package dev.entao.page

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import dev.entao.log.logd
import dev.entao.pages.toast
import dev.entao.views.FrameLayout
import dev.entao.views.beginAnimation

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


class StackPageContainer(activity: PageActivity, frameLayout: FrameLayout) : PageContainer(activity, activity, frameLayout) {


    var animDuration: Long = 500

    //新页面进入,顶部,入栈
    var enterAnim: Animation? = rightInAnim

    //页面关闭,顶部,出栈
    var leaveAnim: Animation? = rightOutAnim

    //变成栈顶
    var resumeAnim: Animation? = alphaInAnim

    //被新页面覆盖
    var pauseAnim: Animation? = alphaOutAnim


    override fun onPageAnimEnter(oldView: View, curView: View) {
        this.enterAnim?.also {
            it.duration = animDuration
            curView.beginAnimation(it) {}
        }
        this.pauseAnim?.also {
            it.duration = animDuration
            oldView.beginAnimation(it) {}
        }
    }

    override fun onPageAnimLeave(oldView: View, curView: View, onOldViewAnimEnd: (View) -> Unit) {
        this.leaveAnim?.also { am ->
            am.duration = animDuration
            logd("begin Anim")
            oldView.beginAnimation(am) {
                logd("end Anim")
                onOldViewAnimEnd(oldView)
            }
        }
        this.resumeAnim?.also { ra ->
            ra.duration = animDuration
            curView.beginAnimation(ra) {

            }
        }
    }


    companion object {
        val rightInAnim: Animation
            get() = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f,
            ).apply {
                this.fillBefore = true
            }
        val rightOutAnim: Animation
            get() = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 1f,
                Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f,
            ).apply {
                this.fillAfter = true
            }

        val alphaInAnim: Animation
            get() = AlphaAnimation(0.3f, 1.0f).apply {
                this.fillBefore = true
            }
        val alphaOutAnim: Animation
            get() = AlphaAnimation(1f, 0.3f).apply {
                this.fillBefore = true
            }

    }


}


