@file:Suppress("MemberVisibilityCanBePrivate")

package dev.entao.page

import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.widget.NestedScrollView
import dev.entao.appbase.*
import dev.entao.base.Progress
import dev.entao.views.*
import dev.entao.views.parentCenter
import dev.entao.views.parentFill

abstract class MsgPage : Page(), MsgListener, Progress {
    var autoBackAction = true
    lateinit var linearContentView: LinearLayout


    val titleBar: TitleBarX by lazy {
        linearContentView.addViewX(TitleBarX(context), 0, Params.linear.height(TitleBarX.HEIGHT).widthFill)
    }

    val topProgress: TopProgressBar by lazy {
        linearContentView.addViewX(TopProgressBar(activity).gone(), 0, Params.linear.widthFill.height(6))
    }

    val loadingView: ProgressBar by lazy {
        pageView.append {
            relativeParams {
                parentCenter.size(56)
            }
            gone()
            isIndeterminate = true
        }
    }

    fun showLoading() {
        if (InMainThread) {
            this.loadingView.bringToFront()
            this.loadingView.visiable()
        } else {
            Task.fore {
                this.loadingView.bringToFront()
                this.loadingView.visiable()
            }
        }
    }

    fun hideLoading() {
        if (InMainThread) {
            this.loadingView.gone()
        } else {
            Task.fore {
                this.loadingView.gone()
            }
        }
    }

    override fun onCreate(pageView: RelativeLayout) {
        super.onCreate(pageView)
        MsgCenter.listenAll(this)
        pageView.apply {
            linearContentView = linearLayoutV {
                relativeParams {
                    parentFill
                }
            }
        }
    }

    override fun onDestroy() {
        MsgCenter.remove(this)
        super.onDestroy()
    }


    override fun onMsg(msg: Msg) {

    }

    override fun onPageCreated() {
        super.onPageCreated()
        if (autoBackAction) {
            onAutoBack()
        }
    }


    private fun onAutoBack() {
        val tb = linearContentView.childViews.firstOrNull { it is TitleBarX } as? TitleBarX ?: return
        val bk = tb.first { it == TitleBarX.BACK }
        if (bk != null) {
            return
        }
        if (stackContainer.bottomPage != this) {
            tb.showBack {
                if (!onBackPressed()) {
                    finishPage()
                }
            }
        }
    }

    override fun onProgressStart(total: Int) {
        topProgress.show(100)
    }

    override fun onProgress(current: Int, total: Int, percent: Int) {
        Task.fore {
            topProgress.visiable()
            topProgress.setProgress(percent)
        }
    }

    override fun onProgressFinish() {
        this.topProgress.postHide(1500)
    }

}

open class LinearPage : MsgPage() {
    var enableContentScroll = false
    lateinit var contentView: LinearLayout


    override fun onCreate(pageView: RelativeLayout) {
        super.onCreate(pageView)
        linearContentView.apply {
            if (enableContentScroll) {
                append<NestedScrollView> {
                    linearParams { fill }
                    needId()
                    contentView = linearLayoutV {
                        frameParams {
                            widthFill.heightWrap
                        }
                    }
                }
            } else {
                contentView = linearLayoutV {
                    linearParams {
                        fill
                    }
                }
            }
        }
        onCreateContent(contentView)
    }

    open fun onCreateContent(contentView: LinearLayout) {

    }
}

open class RelPage : MsgPage() {
    lateinit var contentView: RelativeLayout

    override fun onCreate(pageView: RelativeLayout) {
        super.onCreate(pageView)

        linearContentView.apply {
            relativeLayout {
                linearParams { fill }
                contentView = this
            }
        }
        onCreateContent(contentView)
    }

    open fun onCreateContent(contentView: RelativeLayout) {

    }

}

