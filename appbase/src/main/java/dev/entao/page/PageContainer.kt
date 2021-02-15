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
import dev.entao.views.gone
import dev.entao.views.visiable
import java.util.*
import kotlin.collections.ArrayList


typealias LifeState = Lifecycle.State
typealias LifeEvent = Lifecycle.Event

open class PageQueue(val activity: BaseActivity, private val lifecycleOwner: LifecycleOwner, private val frameLayout: FrameLayout) {
    private val pageQueue: ArrayList<Page> = ArrayList()
    val pageCount: Int get() = pageQueue.size
    var currentPage: Page? = null

    fun getPage(index: Int): Page {
        return pageQueue[index]
    }

    fun addPage(page: Page) {

    }

    fun finishPage(p: Page) {

    }
}

open class PageContainer(val activity: BaseActivity, private val lifecycleOwner: LifecycleOwner, private val frameLayout: FrameLayout) {

    private val pageQueue: ArrayList<Page> = ArrayList()
    val pageCount: Int get() = pageQueue.size

    val topPage: Page? get() = pageQueue.lastOrNull()
    val bottomPage: Page? get() = pageQueue.firstOrNull()

    protected var onlyCurrentVisible: Boolean = false

    var currentPage: Page? = null
        set(value) {
            val old = field
            if (old !== value) {
                if (value != null && value !in pageQueue) error("没有被添加:" + value.pageName)
                field = value
                if (onlyCurrentVisible) {
//                    old?.pageView?.gone()
//                    value?.pageView?.visiable()
                }
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
        if (oldPage == null) {
            if (newPage == null) {
                return
            } else {
                newPage.currentState = ownerState
            }
        } else {
            val oldInQueue = oldPage in pageQueue
            if (oldInQueue) {
                if (ownerState.isAtLeast(LifeState.CREATED)) {
                    oldPage.currentState = LifeState.CREATED
                } else {
                    oldPage.currentState = ownerState  //init or destroyed
                }
                if (newPage == null) {
                    return
                } else {
                    newPage.currentState = ownerState
                    if (!ignoreAnim && ownerState.isAtLeast(LifeState.STARTED)) {
                        onPageAnimEnter(oldPage.pageView, newPage.pageView)
                    }
                }
            } else {
                if (newPage == null) {
                    frameLayout.removeView(oldPage.pageView)
                } else {
                    newPage.currentState = ownerState
                    if (!ignoreAnim && ownerState.isAtLeast(LifeState.STARTED)) {
                        onPageAnimLeave(oldPage.pageView, newPage.pageView) {
                            frameLayout.removeView(it)
                        }
                    } else {
                        frameLayout.removeView(oldPage.pageView)
                    }
                }
            }

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
//        page.onAttach(this)
        page.currentState = LifeState.INITIALIZED
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
                            frameLayout.removeView(p.pageView)//not reach!
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

}


