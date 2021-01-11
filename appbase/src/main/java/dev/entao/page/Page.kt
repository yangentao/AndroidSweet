@file:Suppress("MemberVisibilityCanBePrivate")

package dev.entao.page

import android.content.Context
import android.view.KeyEvent
import android.view.animation.Animation
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import dev.entao.views.backColorWhite

abstract class Page : LifecycleOwner, LifecycleEventObserver {
    val pageId: Int = currentPageId_++
    val pageName: String = this::class.simpleName + "_$pageId" // for debug only
    lateinit var pageView: RelativeLayout

    internal val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }
    internal var finishAnim: Animation? = null

    //即使当前页已经finish, 也要保持pageManager的引用, 因为可能会调用当前页面的pushPage方法.
    //并不会造成内存泄漏问题,
    lateinit var pageManager: PageManager
    val context: Context get() = pageManager.activity
    val activity: PageActivity get() = pageManager.activity

    val currentState: Lifecycle.State get() = this.lifecycleRegistry.currentState

    val isResumed: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.RESUMED)

    val isStarted: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.STARTED)

    val isCreated: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.CREATED)

    val isDestroyed: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.DESTROYED)


    val isTopPage: Boolean get() = pageManager.topPage == this
    val isBottomPage: Boolean get() = pageManager.bottomPage == this

    fun pushPage(p: Page) {
        pageManager.pushPage(p)
    }

    fun <T : Page> pushPage(p: T, block: T.() -> Unit) {
        p.block()
        pageManager.pushPage(p)
    }

    fun finishPage() {
        pageManager.finishPage(this)
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    fun onAttach(pm: PageManager) {
//        logd(pageName, ".ON_ATTACH")
        this.pageManager = pm
        lifecycleRegistry.addObserver(this)
        pageView = RelativeLayout(this.context).apply {
            backColorWhite()
        }
    }

    fun onDetach() {
//        logd(pageName, ".ON_DETACH")
        lifecycleRegistry.removeObserver(this)

    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    open fun onCreate(pageView: RelativeLayout) {

    }

    open fun onPageCreated() {}


    open fun onStart() {
    }


    open fun onResume() {
    }


    open fun onPause() {
    }


    open fun onStop() {
    }


    open fun onDestroy() {
    }

    open fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    open fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (this === source) {
//            logd("PageState: ", pageId, event)
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
//                    logd(pageName, ".ON_CREATE")
                    onCreate(this.pageView)
                    pageManager.onPageViewCreated(this)
                    onPageCreated()
                }
                Lifecycle.Event.ON_START -> {
//                    logd(pageName, ".ON_START")
                    onStart()
                }
                Lifecycle.Event.ON_RESUME -> {
//                    logd(pageName, ".ON_RESUME")
                    onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
//                    logd(pageName, ".ON_PAUSE")
                    onPause()
                }
                Lifecycle.Event.ON_STOP -> {
//                    logd(pageName, ".ON_STOP")
                    onStop()
                }
                Lifecycle.Event.ON_DESTROY -> {
//                    logd(pageName, ".ON_DESTROY")
                    onDestroy()
                    lifecycleRegistry.removeObserver(this)
                    pageManager.onPageDestroyed(this)
                }
                Lifecycle.Event.ON_ANY -> {

                }
            }
        }
    }

    companion object {
        private var currentPageId_: Int = 1
    }


}