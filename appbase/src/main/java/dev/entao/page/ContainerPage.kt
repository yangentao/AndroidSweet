package dev.entao.page

import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.entao.views.*


//TODO container page
class ContainerPage : Page() {

    private val pageList: ArrayList<Page> = ArrayList()
    private lateinit var frameLayout: FrameLayout

    var currentIndex: Int = 0
        set(value) {
            if (field == value) return
            if (value !in pageList.indices) error("currentIndex超出范围")
            val old = field
            field = value
            onCurrentChanged(old, value)
        }

    private fun onCurrentChanged(old: Int, newValue: Int) {
        checkChildVisible()
    }

    var pages: List<Page>
        get() = pageList.toList()
        set(value) {
            pageList.clear()
            pageList.addAll(value)
        }

//    fun addPage(page: Page) {
//        if (pageList.contains(page)) return
//        pageList.add(page)
//        if (attached) {
//            page.onAttach(this.pageManager)
//            if (this.currentState.isAtLeast(LifeState.CREATED)) {
//                page.currentState = LifeState.CREATED
//            }
//            page.lifecycle.addObserver(this)
//        }
//        checkChildVisible()
//    }
//
//    fun removePage(page: Page) {
//        if (page !in pageList) return
//        if (page.attached) {
//            page.currentState = LifeState.DESTROYED
//        } else {
//            pageList.remove(page)
//        }
//        checkChildVisible()
//    }

    val currPage: Page?
        get() {
            if (currentIndex in pageList.indices) {
                return pageList[currentIndex]
            }
            return null
        }

    var currentPage: Page
        get() {
            if (currPage == null) {
                error("没有设置当前Page")
            }
            return currPage!!
        }
        set(value) {
            if (value !in pageList) error("要设置的当前页面没有被添加")
            if (value === currPage) return
            currPage?.also { old ->
                old.pageView.animation?.cancel()
                when (old.currentState) {
                    Lifecycle.State.STARTED, Lifecycle.State.RESUMED -> {
                        old.currentState = Lifecycle.State.CREATED
                    }
                    else -> {
                    }
                }
            }
            value.currentState = this.currentState
            checkChildVisible()
        }

    private fun checkChildVisible() {
        if (!this.currentState.isAtLeast(LifeState.CREATED)) return
        for (v in this.frameLayout.childViews) {
            if (v === currPage) {
                v.visiable()
            } else {
                v.invisiable()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (this.currPage?.onBackPressed() == true) {
            return true
        }
        return super.onBackPressed()
    }

//    fun onAttach(pm: PageContainer) {
//        super.onAttach(pm)
//        frameLayout = FrameLayout(context)
//        this.pageView.addView(frameLayout, Params.relative.fill)
//        this.pageList.forEach {
//            it.onAttach(pm)
//            it.lifecycle.addObserver(this)
//        }
//    }

    override fun onDetach() {
        this.pageList.forEach {
            it.onDetach()
            it.lifecycle.removeObserver(this)
        }
        super.onDetach()
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        super.onStateChanged(source, event)
        if (source === this) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    pageList.forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(event)
                    }
                    checkChildVisible()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    pageList.forEach {
                        it.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    }
                }
                Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    currPage?.lifecycleRegistry?.handleLifecycleEvent(event)
                }
                Lifecycle.Event.ON_ANY -> {

                }
            }
        } else if (source is Page) {
            val p: Page = source
            when (event) {
                LifeEvent.ON_CREATE -> {
                    if (p.pageView.layoutParams is FrameParams) {
                        frameLayout.addView(p.pageView)
                    } else {
                        frameLayout.addView(p.pageView, FrameParams.MATCH_PARENT, FrameParams.MATCH_PARENT)
                    }
                }
                LifeEvent.ON_DESTROY -> {
                    p.lifecycle.removeObserver(this)
                    val oldIndex = pageList.indexOf(p)
                    frameLayout.removeView(p.pageView)
                    p.onDetach()
                    pageList.remove(p)
                    if (this.currentState.isAtLeast(LifeState.CREATED) && p === currPage && pageList.isNotEmpty()) {
                        val n = when {
                            oldIndex >= pageList.size -> {
                                pageList.size - 1
                            }
                            oldIndex < 0 -> {
                                0
                            }
                            else -> {
                                oldIndex
                            }
                        }
//                        currPage = pageList[n]
                        currPage?.currentState = this.lifecycle.currentState
                    }
                }
                else -> {
                }
            }
        }

    }

}