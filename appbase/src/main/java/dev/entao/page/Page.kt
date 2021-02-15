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
import dev.entao.log.logd
import dev.entao.pages.hideInputMethod
import dev.entao.views.backColorWhite
import dev.entao.views.gone
import dev.entao.views.onClick
import dev.entao.views.visiable

abstract class Page : LifecycleOwner, LifecycleEventObserver {
    val pageId: Int = currentPageId_++
    val pageName: String = this::class.qualifiedName + "@$pageId" // for debug only
    lateinit var pageView: RelativeLayout
        private set

    internal val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }

    //即使当前页已经finish, 也要保持pageManager的引用, 因为可能会调用当前页面的pushPage方法.
    //并不会造成内存泄漏问题,
    lateinit var stackContainer: StackContainer
        private set

    val context: Context get() = stackContainer.activity
    val activity: BaseActivity get() = stackContainer.activity

    var attached: Boolean = false
        private set

    var currentState: Lifecycle.State
        get() = this.lifecycleRegistry.currentState
        internal set(value) {
            this.lifecycleRegistry.currentState = value
        }

    val isResumed: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.RESUMED)

    val isStarted: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.STARTED)

    val isCreated: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.CREATED)

    val isDestroyed: Boolean
        get() = currentState.isAtLeast(Lifecycle.State.DESTROYED)


    val isTopPage: Boolean get() = stackContainer.topPage == this
    val isBottomPage: Boolean get() = stackContainer.bottomPage == this

    fun pushPage(p: Page) {
        stackContainer.pushPage(p)
    }

    fun <T : Page> pushPage(p: T, block: T.() -> Unit) {
        p.block()
        stackContainer.pushPage(p)
    }

    open fun finishPage() {
        stackContainer.finishPage(this)
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    open fun onAttach(pm: StackContainer) {
        this.stackContainer = pm
        pageView = RelativeLayout(this.context).apply {
            backColorWhite()
            isClickable = true
            isFocusable = true
            isFocusableInTouchMode = true
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    activity.hideInputMethod()
                }
            }
        }
        attached = true
        lifecycleRegistry.addObserver(this)
    }

    open fun onDetach() {
        lifecycleRegistry.removeObserver(this)
        attached = false

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
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    onCreate(this.pageView)
                    onPageCreated()
                }
                Lifecycle.Event.ON_START -> {
                    this.pageView.visiable()
                    onStart()
                }
                Lifecycle.Event.ON_RESUME -> {
                    onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    onPause()
                }
                Lifecycle.Event.ON_STOP -> {
                    onStop()
                    this.pageView.gone()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    onDestroy()
                    lifecycleRegistry.removeObserver(this)
                }
                Lifecycle.Event.ON_ANY -> {

                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Page) {
            return this.pageId == other.pageId
        }
        return false
    }

    override fun hashCode(): Int {
        return this.pageId
    }

    companion object {
        private var currentPageId_: Int = 1
    }
}
