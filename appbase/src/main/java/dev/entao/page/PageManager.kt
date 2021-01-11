@file:Suppress("unused")

package dev.entao.page

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.entao.pages.hideInputMethod
import dev.entao.views.FrameParams
import dev.entao.views.onClick
import dev.entao.log.logd
import dev.entao.log.loge
import java.util.*

//为什么重新发明轮子?
//为什么不使用Activity, Activity传递对象类型的参数和回调很困难
//为什么不使用Fragment, Fragment的onPause在当前Fragment被其他Fragmeng覆盖时不会被调用,它的状态跟Activity同步
//为什么不使用navigation-fragmen包, 同上两条
class PageManager(val activity: PageActivity, private val frameLayout: FrameLayout) : LifecycleEventObserver {

    private val pageStack: Stack<Page> = Stack()
    val pageCount: Int get() = pageStack.size
    val topPage: Page? get() = pageStack.top()
    val bottomPage: Page? get() = pageStack.firstOrNull()

    var animDuration: Long = 200

    //新页面进入,顶部,入栈
    var enterAnim: Animation? = rightInAnim

    //页面关闭,顶部,出栈
    var leaveAnim: Animation? = rightOutAnim

    //变成栈顶
    var resumeAnim: Animation? = alphaInAnim

    //被新页面覆盖
    var pauseAnim: Animation? = alphaOutAnim

    private var ignoreAnim = false

    init {
        activity.lifecycle.addObserver(this)
    }

    fun setContentPage(p: Page) {
        ignoreAnim = true
        while (!pageStack.empty()) {
            popPage()
        }
        pushPage(p)
        ignoreAnim = false
    }


    fun pushPage(page: Page) {
        if (activity.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            loge("Activity Already Destroyed! Cannot Push Any Page. ", page::class.qualifiedName)
            return
        }
        if (pageStack.contains(page)) {
            return
        }
        pageStack.top()?.also { tp ->
            tp.pageView.animation?.cancel()
            when (tp.lifecycleRegistry.currentState) {
                Lifecycle.State.STARTED, Lifecycle.State.RESUMED -> {
                    tp.lifecycleRegistry.currentState = Lifecycle.State.CREATED
                }
                else -> {
                }
            }
        }
        logd(page.pageName, "push to Stack ")
        pageStack.push(page)
        page.onAttach(this)
        page.lifecycleRegistry.currentState = activity.lifecycle.currentState
    }

    //只保留栈底
    fun popToBottom() {
        ignoreAnim = true
        while (pageStack.size > 1) {
            val p = pageStack.pop()
            finishPage(p)
        }
        ignoreAnim = false
    }

    fun popPage() {
        val p = pageStack.top() ?: return
        finishPage(p)

    }

    fun finishPage(p: Page) {
        logd("finishPage: ", p.pageName)
        p.pageView.animation?.cancel()
        p.lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }


    internal fun onPageViewCreated(p: Page) {
        p.pageView.apply {
            isClickable = true
            isFocusable = true
            isFocusableInTouchMode = true
            onClick {
                activity.hideInputMethod()
            }
        }

        if (p.pageView.layoutParams is FrameParams) {
            frameLayout.addView(p.pageView)
        } else {
            frameLayout.addView(p.pageView, FrameParams.MATCH_PARENT, FrameParams.MATCH_PARENT)
        }
        if (!ignoreAnim && pageStack.top() == p && pageStack.size > 1) {
            this.enterAnim?.also {
                it.duration = animDuration
                p.pageView.beginAnimation(it) {}
            }
            this.pauseAnim?.also {
                it.duration = animDuration
                pageStack[pageStack.size - 2].pageView.beginAnimation(it) {}
            }
        }
    }

    private fun afterPageDestroyed(p: Page) {
        frameLayout.removeView(p.pageView)
        p.onDetach()
        pageStack.top()?.lifecycleRegistry?.currentState = activity.lifecycle.currentState
    }

    internal fun onPageDestroyed(p: Page) {
        p.lifecycle.removeObserver(this)
        logd(p.pageName, "remove from Stack")
        val isTopPage = p === pageStack.top()
        pageStack.remove(p)
        if (!ignoreAnim && isTopPage && pageStack.isNotEmpty() && this.leaveAnim != null) {
            this.leaveAnim?.also { am ->
                am.duration = animDuration
                logd("begin Anim")
                p.pageView.beginAnimation(am) {
                    logd("end Anim")
                    afterPageDestroyed(p)
                }
            }

            this.resumeAnim?.also { ra ->
                pageStack.top()?.also {
                    ra.duration = animDuration
                    it.pageView.beginAnimation(ra) {}
                }

            }
        } else {
            afterPageDestroyed(p)
        }
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source is AppCompatActivity) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    pageStack.forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(event)
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    pageStack.reversed().forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    }
                }
                Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    pageStack.top()?.lifecycleRegistry?.handleLifecycleEvent(event)
                }


                Lifecycle.Event.ON_ANY -> {

                }
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
            get() = AlphaAnimation(0.2f, 1.0f).apply {
                this.fillBefore = true
            }
        val alphaOutAnim: Animation
            get() = AlphaAnimation(1f, 0.2f).apply {
                this.fillBefore = true
            }

    }


}

fun <T : Any> Stack<T>.top(): T? {
    return this.lastOrNull()
}

fun <T : Any> Stack<T>.popOrNull(): T? {
    return this.removeLastOrNull()
}

fun View.beginAnimation(animation: Animation, onEndCallback: () -> Unit) {
    this.animation?.cancel()
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            onEndCallback()
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }

    })
    this.startAnimation(animation)
}