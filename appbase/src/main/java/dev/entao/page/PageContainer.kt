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
import dev.entao.log.logd
import dev.entao.views.FrameParams
import java.util.*
import kotlin.collections.ArrayList


typealias LifeState = Lifecycle.State
typealias LifeEvent = Lifecycle.Event

open class PageContainer(val activity: PageActivity, val lifecycleOwner: LifecycleOwner, val frameLayout: FrameLayout) {

    protected val pageQueue: ArrayList<Page> = ArrayList()
    val pageCount: Int get() = pageQueue.size

    val topPage: Page? get() = pageQueue.lastOrNull()
    val bottomPage: Page? get() = pageQueue.firstOrNull()

    var currentPage: Page? = null
        set(value) {
            val old = field
            if (old != value) {
                if (value != null && value !in pageQueue) error("没有被添加:" + value.pageName)
                field = value
                onCurrentPageChanged(old, value)
            }
        }

    private val ownerState: LifeState get() = lifecycleOwner.lifecycle.currentState


    private var ignoreAnim = false

    private val lifeObserver = LifecycleEventObserver { source, event -> this@PageContainer.onStateChanged(source, event) }


    init {
        lifecycleOwner.lifecycle.addObserver(lifeObserver)
    }

    fun getPage(index: Int): Page {
        return pageQueue[index]
    }

    protected open fun onCurrentPageChanged(oldPage: Page?, newPage: Page?) {
        if (oldPage in pageQueue) {
            oldPage?.currentState = LifeState.CREATED
        }
        logd("Container State: ", ownerState)
        if (ownerState.isAtLeast(LifeState.CREATED)) {
            newPage?.currentState = ownerState
        } else {
            newPage?.currentState = LifeState.CREATED
        }
        if (ignoreAnim || !ownerState.isAtLeast(LifeState.STARTED)) return
        val oldView: View = oldPage?.pageView ?: return
        val newView: View = newPage?.pageView ?: return
        val isLeave = oldPage !in pageQueue
        if (isLeave) {
            onPageAnimLeave(oldView, newView) {
                frameLayout.removeView(it)
            }
        } else {
            onPageAnimEnter(oldView, newView)
        }
    }

    protected open fun onPageAnimEnter(oldView: View, curView: View) {

    }

    protected open fun onPageAnimLeave(oldView: View, curView: View, onOldViewAnimEnd: (View) -> Unit) {
        onOldViewAnimEnd(oldView)
    }

    fun addPage(page: Page) {
        if (ownerState == Lifecycle.State.DESTROYED) {
            error("Activity Already Destroyed! Cannot Push Any Page. " + page::class.qualifiedName)
        }
        if (pageQueue.contains(page)) {
            error("Already Exist Page:" + page::class.qualifiedName)
        }
        pageQueue.add(page)
        page.lifecycle.addObserver(this.lifeObserver)
        page.onAttach(this)
        page.currentState = LifeState.CREATED
        logd("PageAdded:", page.pageName)
        logd("PageQueue: ", pageQueue.joinToString(",") { it.pageName })
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

    protected open fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        logd(source::class.simpleName + " StateChanged:", source.lifecycle.currentState, event)
        if (source === lifecycleOwner) {
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

                }
                Lifecycle.Event.ON_DESTROY -> {
                    p.lifecycle.removeObserver(lifeObserver)
                    p.onDetach()
                    if (p == currentPage) {
                        val oldIndex = pageQueue.indexOf(p)
                        val newCurr = onCurrentFinished(oldIndex)
                        pageQueue.remove(p)
                        if (currentPage == newCurr) {
                            frameLayout.removeView(p.pageView)
                        } else {
                            currentPage = newCurr
                        }
                        if (pageQueue.isEmpty()) {
                            onPageQueueEmpty()
                        }
                    } else {
                        pageQueue.remove(p)
                        frameLayout.removeView(p.pageView)
                        if (pageQueue.isEmpty()) {
                            onPageQueueEmpty()
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


