@file:Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue")

package dev.entao.page

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.entao.log.logd
import dev.entao.views.FrameParams
import dev.entao.views.beginAnimation
import dev.entao.views.gone
import dev.entao.views.visiable

open class StackContainer(val activity: StackActivity, private val frameLayout: FrameLayout) {
    private val pageQueue: ArrayList<Page> = ArrayList()
    val pageCount: Int get() = pageQueue.size
    val lifecycleOwner: LifecycleOwner = activity

    val topPage: Page? get() = pageQueue.lastOrNull()
    val bottomPage: Page? get() = pageQueue.firstOrNull()

    var animDuration: Long = 240

    //新页面进入,顶部,入栈
    var animEnter: Animation? = rightInAnim

    //页面关闭,顶部,出栈
    var animLeave: Animation? = rightOutAnim

    //变成栈顶
    var animResume: Animation? = alphaInAnim

    //被新页面覆盖
    var animPause: Animation? = alphaOutAnim

    private val lifeObserver = LifecycleEventObserver { source, event -> onStateChangedEvent(source, event) }


    init {
        lifecycleOwner.lifecycle.addObserver(lifeObserver)
    }

    fun getPage(index: Int): Page {
        return pageQueue[index]
    }

    private fun addPage(page: Page, anim: Boolean) {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        if (pageQueue.contains(page)) {
            error("Already Exist Page:" + page::class.qualifiedName)
        }
        val oldPage = topPage
        pageQueue.add(page)
        page.onAttach(this)
        page.lifecycle.addObserver(this.lifeObserver)
        if (page.pageView.layoutParams is FrameParams) {
            frameLayout.addView(page.pageView)
        } else {
            frameLayout.addView(page.pageView, FrameParams.MATCH_PARENT, FrameParams.MATCH_PARENT)
        }
        val currState = lifecycleOwner.lifecycle.currentState
        page.currentState = currState
        oldPage?.currentState = LifeState.CREATED
        if (anim && oldPage != null && currState.isAtLeast(LifeState.STARTED)) {
            val eA = animEnter ?: return

            val pA = animPause ?: noChangeAnim
            pA.duration = animDuration
            oldPage.pageView.beginAnimation(pA)

            eA.duration = animDuration
            page.pageView.beginAnimation(eA)
        }
    }

    private fun removePage(page: Page, anim: Boolean) {
        page.pageView.animation?.cancel()
//        page.lifecycle.removeObserver(lifeObserver)
        page.currentState = Lifecycle.State.DESTROYED
        page.onDetach()
        page.lifecycle.removeObserver(this.lifeObserver)
        if (pageQueue.size <= 1 || this.topPage != page) {
            pageQueue.remove(page)
            frameLayout.removeView(page.pageView)
            if (pageQueue.isEmpty()) {
                onPageQueueEmpty()
            }
            return
        }
        //p is top page, need anim
        val newTopPage = pageQueue[pageQueue.size - 2]
        pageQueue.remove(page)
        newTopPage.currentState = lifecycleOwner.lifecycle.currentState
        val la = animLeave
        if (!anim || la == null || !newTopPage.currentState.isAtLeast(LifeState.STARTED)) {
            frameLayout.removeView(page.pageView)
            return
        }
        la.duration = animDuration
        page.pageView.beginAnimation(la) {
            frameLayout.removeView(page.pageView)
        }
        this.animResume?.duration = animDuration
        newTopPage.pageView.beginAnimation(this.animResume)

    }


    fun pushPage(page: Page) {
        addPage(page, true)
    }

    fun popPage() {
        val p = topPage ?: return
        finishPage(p)

    }

    fun setContentPage(p: Page) {
        pushPage(p)
        while (pageQueue.size > 1) {
            removePage(pageQueue[pageQueue.size - 2], false)
        }
    }

    //只保留栈底
    fun popToBottom() {
        while (pageQueue.size > 1) {
            val p = topPage ?: return
            removePage(p, false)
        }
    }


    fun finishAll() {
        while (pageQueue.isNotEmpty()) {
            removePage(topPage!!, false)
        }
    }

    fun finishPage(p: Page) {
        removePage(p, true)
    }

    protected open fun onPageQueueEmpty() {

    }

    protected open fun onStateChangedEvent(source: LifecycleOwner, event: Lifecycle.Event) {
        logd("Stack: Source=", source::class.simpleName + " State=", source.lifecycle.currentState, " Event=", event)
        if (source === lifecycleOwner) {
            val pages = pageQueue.toList()
            when (event) {
                LifeEvent.ON_CREATE -> {
                    pages.forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(event)
                    }
                }
                LifeEvent.ON_DESTROY -> {
                    finishAll()
                }
                LifeEvent.ON_START -> {
                    val tp = topPage
                    pages.forEach { p ->
                        if (p == tp) {
                            p.lifecycleRegistry.handleLifecycleEvent(event)
                            p.pageView.visiable()
                        } else {
                            p.currentState = LifeState.CREATED
                            p.pageView.gone()
                        }
                    }
                }
                LifeEvent.ON_STOP -> {
                    val tp = topPage
                    pages.forEach { p ->
                        if (p == tp) {
                            p.lifecycleRegistry.handleLifecycleEvent(event)
                        } else {
                            p.currentState = LifeState.CREATED
                        }
                        p.pageView.gone()
                    }
                }
                LifeEvent.ON_RESUME, LifeEvent.ON_PAUSE -> {
                    topPage?.lifecycleRegistry?.handleLifecycleEvent(event)
                }
                else -> {
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
            get() = AlphaAnimation(0.5f, 1.0f).apply {
                this.fillBefore = true
            }
        val alphaOutAnim: Animation
            get() = AlphaAnimation(1f, 0.5f).apply {
                this.fillBefore = true
            }

        val noChangeAnim: Animation
            get() = AlphaAnimation(1f, 1f).apply {
                this.fillBefore = true
            }

    }

}


