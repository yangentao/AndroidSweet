package dev.entao.page

import android.view.View
import android.widget.FrameLayout
import dev.entao.views.invisiable
import dev.entao.views.visiable


class TabPageContainer(tabPage: Page, frameLayout: FrameLayout) : PageContainer(tabPage.activity, tabPage, frameLayout) {


    override fun onPageAnimEnter(oldView: View, curView: View) {
        oldView.invisiable()
        curView.visiable()

    }

    override fun onPageAnimLeave(oldView: View, curView: View, onOldViewAnimEnd: (View) -> Unit) {
        oldView.invisiable()
        curView.visiable()
        onOldViewAnimEnd(oldView)

    }


}

