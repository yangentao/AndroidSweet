@file:Suppress("unused")

package dev.entao.page

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.entao.views.FrameParams
import java.util.*
import kotlin.collections.ArrayList

open class PageContainer(val activity: PageActivity, val containerLifecycle: Lifecycle, val frameLayout: FrameLayout) {

    protected val pageQueue: ArrayList<Page> = ArrayList()
    val pageCount: Int get() = pageQueue.size

    var currentPage: Page? = null
        set(value) {
            val old = field
            if (old != value) {
                if (value !in pageQueue) error("没有被添加")
                field = value
                onCurrentPageChanged(old, value)
            }
        }


    protected var ignoreAnim = false

    private val lifeObserver = LifecycleEventObserver { source, event -> this@PageContainer.onStateChanged(source, event) }


    init {
        containerLifecycle.addObserver(lifeObserver)
    }

    fun getPage(index: Int): Page {
        return pageQueue[index]
    }

    private fun onCurrentPageChanged(oldPage: Page?, newPage: Page?) {
        if (oldPage in pageQueue) {
            oldPage?.currentState = LifeState.CREATED
        }
        newPage?.currentState = containerLifecycle.currentState
    }

    fun addPage(page: Page) {
        if (containerLifecycle.currentState == Lifecycle.State.DESTROYED) {
            error("Activity Already Destroyed! Cannot Push Any Page. " + page::class.qualifiedName)
        }
        if (pageQueue.contains(page)) {
            error("Already Exist Page:" + page::class.qualifiedName)
        }
        pageQueue.add(page)
        page.lifecycle.addObserver(this.lifeObserver)
        page.onAttach(this)
        page.currentState = LifeState.CREATED
    }


    fun pushPage(page: Page) {
        addPage(page)
        currentPage = page
    }

    fun popPage() {
        val p = pageQueue.lastOrNull() ?: return
        finishPage(p)

    }

    fun setContentPage(p: Page) {
        ignoreAnim = true
        pushPage(p)
        while (pageQueue.size > 1) {
            finishPage(pageQueue[pageQueue.size - 2])
        }
        ignoreAnim = false
    }

    //只保留栈底
    fun popToBottom() {
        ignoreAnim = true
        while (pageQueue.size > 1) {
            val p = pageQueue.removeLast()
            finishPage(p)
        }
        ignoreAnim = false
    }


    fun finishAll() {
        ignoreAnim = true
        pageQueue.reversed().forEach { p ->
            finishPage(p)
        }
        ignoreAnim = false
    }

    fun finishPage(p: Page) {
        p.pageView.animation?.cancel()
        p.currentState = Lifecycle.State.DESTROYED

    }

    protected open fun onPageQueueEmpty() {

    }


    protected open fun onCurrentFinished(index: Int): Page? {
        val n = if (index == pageQueue.size - 1) {
            pageQueue.size - 2
        } else {
            index
        }
        if (n >= 0) {
            return pageQueue[n]
        }
        return null
    }

    protected open fun onPageCreateAnim(page: Page) {

    }

    protected open fun onPageDestroyAnim(page: Page, onAnimEnd: (Page) -> Unit) {
        onAnimEnd(page)
    }


    protected open fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source is AppCompatActivity) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    pageQueue.forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(event)
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    pageQueue.reversed().forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(event)
                    }
                }
                Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    currentPage?.lifecycleRegistry?.handleLifecycleEvent(event)
                }
                Lifecycle.Event.ON_ANY -> {

                }
            }
        } else if (source is Page) {
            val p: Page = source
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    if (p.pageView.layoutParams is FrameParams) {
                        frameLayout.addView(p.pageView)
                    } else {
                        frameLayout.addView(p.pageView, FrameParams.MATCH_PARENT, FrameParams.MATCH_PARENT)
                    }
                    if (!ignoreAnim) {
                        onPageCreateAnim(p)
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    p.lifecycle.removeObserver(lifeObserver)
                    p.onDetach()
                    if (p === currentPage) {
                        val oldIndex = pageQueue.indexOf(p)
                        currentPage = onCurrentFinished(oldIndex)
                    }
                    pageQueue.remove(p)
                    when {
                        pageQueue.isEmpty() -> {
                            frameLayout.removeView(p.pageView)
                            onPageQueueEmpty()
                        }
                        ignoreAnim -> {
                            frameLayout.removeView(p.pageView)
                        }
                        else -> {
                            onPageDestroyAnim(p) {
                                frameLayout.removeView(it.pageView)
                            }
                        }
                    }
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
            get() = AlphaAnimation(0.2f, 1.0f).apply {
                this.fillBefore = true
            }
        val alphaOutAnim: Animation
            get() = AlphaAnimation(1f, 0.2f).apply {
                this.fillBefore = true
            }

    }


}


