@file:Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue")

package dev.entao.page

import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.entao.log.logd
import dev.entao.views.FrameParams
import dev.entao.views.gone
import dev.entao.views.visiable

open class TabContainer(val tabPage: Page, private val frameLayout: FrameLayout) {
    private val lifecycleOwner: LifecycleOwner = tabPage.pageManager.lifecycleOwner
    private val pageList: ArrayList<Page> = ArrayList()
    val pageCount: Int get() = pageList.size

    private val currentState: LifeState get() = lifecycleOwner.lifecycle.currentState

    var currentIndex: Int = -1
        set(value) {
            if (value in pageList.indices) {
                field = value
                updateVisiable()
            }
        }


    val currentPage: Page?
        get() {
            if (currentIndex in pageList.indices) {
                return pageList[currentIndex]
            }
            return null
        }


    private val lifeObserver = LifecycleEventObserver { source, event -> onStateChangedEvent(source, event) }


    init {
        lifecycleOwner.lifecycle.addObserver(lifeObserver)
    }

    fun getPage(index: Int): Page {
        return pageList[index]
    }

    private fun updateVisiable() {
        val ls = pageList.toList()
        val st = currentState
        val curr = this.currentIndex
        for (n in ls.indices) {
            val page = ls[n]
            if (n == curr) {
                page.pageView.visiable()
                page.currentState = st
            } else {
                page.pageView.gone()
                if (st.isAtLeast(LifeState.CREATED)) {
                    page.currentState = LifeState.CREATED
                }
            }
        }
    }

    fun addPage(page: Page) {
        if (currentState == Lifecycle.State.DESTROYED) {
            return
        }
        if (page in pageList) {
            error("Already Exist Page:" + page::class.qualifiedName)
        }
        pageList.add(page)
        page.onAttach(tabPage.pageManager)
        if (page.pageView.layoutParams is FrameParams) {
            frameLayout.addView(page.pageView)
        } else {
            frameLayout.addView(page.pageView, FrameParams.MATCH_PARENT, FrameParams.MATCH_PARENT)
        }
        page.pageView.gone()
        if (currentState.isAtLeast(LifeState.CREATED)) {
            page.currentState = LifeState.CREATED
        }
    }

    fun removePage(page: Page) {
        page.pageView.animation?.cancel()
        page.currentState = LifeState.DESTROYED
        page.onDetach()
        pageList.remove(page)
        frameLayout.removeView(page.pageView)
        updateVisiable()
        if (pageList.isEmpty()) {
            onPageQueueEmpty()
        }
    }


    fun removeAll() {
        val ls = this.pageList.toList()
        ls.forEach {
            it.pageView.animation?.cancel()
            it.currentState = LifeState.DESTROYED
            it.onDetach()
        }
        pageList.removeAll(ls)
        for (p in ls) {
            frameLayout.removeView(p.pageView)
        }
        if (pageList.isEmpty()) {
            onPageQueueEmpty()
        }
    }


    protected open fun onPageQueueEmpty() {

    }

    protected open fun onStateChangedEvent(source: LifecycleOwner, event: Lifecycle.Event) {
        logd(source::class.simpleName + " StateChanged:", source.lifecycle.currentState, event)
        if (source === lifecycleOwner) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    pageList.toList().forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(event)
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    removeAll()
                }
                Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    val ls = pageList.toList()
                    for (n in ls.indices) {
                        val page = ls[n]
                        if (n == currentIndex) {
                            page.lifecycleRegistry.handleLifecycleEvent(event)
                            page.pageView.visiable()
                        } else {
                            page.pageView.gone()
                            page.currentState = LifeState.CREATED
                        }
                    }
                }
                else -> {

                }
            }
        }

    }

}


