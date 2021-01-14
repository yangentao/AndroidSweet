@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.page

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import dev.entao.log.logd
import dev.entao.views.beginAnimation


class StackPageContainer(activity: PageActivity, frameLayout: FrameLayout) : PageContainer(activity, activity, frameLayout) {


    var animDuration: Long = 500

    //新页面进入,顶部,入栈
    var enterAnim: Animation? = rightInAnim

    //页面关闭,顶部,出栈
    var leaveAnim: Animation? = rightOutAnim

    //变成栈顶
    var resumeAnim: Animation? = alphaInAnim

    //被新页面覆盖
    var pauseAnim: Animation? = alphaOutAnim


    override fun onPageAnimEnter(oldView: View, curView: View) {
        this.enterAnim?.also {
            it.duration = animDuration
            curView.beginAnimation(it) {}
        }
        this.pauseAnim?.also {
            it.duration = animDuration
            oldView.beginAnimation(it) {}
        }
    }

    override fun onPageAnimLeave(oldView: View, curView: View, onOldViewAnimEnd: (View) -> Unit) {
        this.leaveAnim?.also { am ->
            am.duration = animDuration
            logd("begin Anim")
            oldView.beginAnimation(am) {
                logd("end Anim")
                onOldViewAnimEnd(oldView)
            }
        }
        this.resumeAnim?.also { ra ->
            ra.duration = animDuration
            curView.beginAnimation(ra) {

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
            get() = AlphaAnimation(0.3f, 1.0f).apply {
                this.fillBefore = true
            }
        val alphaOutAnim: Animation
            get() = AlphaAnimation(1f, 0.3f).apply {
                this.fillBefore = true
            }

    }


}

