package dev.entao.page

import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.entao.log.logd
import dev.entao.views.*


class TabPageItem(val title: String, val icon: Int, val page: Page)

class TabPage : Page() {
    var bottomNavStyle = com.google.android.material.R.style.Widget_MaterialComponents_BottomNavigationView
    lateinit var container: TabPageContainer
    lateinit var navView: BottomNavigationView
    lateinit var frameView: FrameLayout
    private val items: ArrayList<TabPageItem> = ArrayList()
    private var currentTab: Int = 0

    fun setTabs(items: List<TabPageItem>) {
        this.items.clear()
        this.items.addAll(items)
        if (this.attached) {
            this.container.finishAll()
            navView.menu.clear()
            for (item in items) {
                addItemToContainer(item)
            }
        }
    }

    fun addTab(item: TabPageItem) {
        items.add(item)
        if (this.attached) {
            addItemToContainer(item)
        }

    }

    fun selectTab(n: Int) {
        if (n in items.indices) {
            currentTab = n
            if (attached) {
                navView.menu.getItem(n).isChecked = true
                container.currentPage = items[n].page
            }
        }
    }

    fun removeTab(n: Int) {
        if (n in items.indices) {
            val item = items.removeAt(n)
            if (attached) {
                container.finishPage(item.page)
                val mid = navView.menu.getItem(n).itemId
                navView.menu.removeItem(mid)

                if (currentTab >= items.size) {
                    currentTab -= 1
                }
                if (currentTab in items.indices) {
                    selectTab(currentTab)
                }
            }
        }
    }

    private fun addItemToContainer(item: TabPageItem) {
        container.addPage(item.page)
        navView.menu.add(item.title).apply {
            this.setIcon(item.icon)
        }
    }

    private fun selByUser(mitem: MenuItem): Boolean {
        logd("sel by user: ", mitem.title)
        val item = items.first { it.title == mitem.title } ?: return false
        container.currentPage = item.page
        return true
    }

    override fun onAttach(pm: PageContainer) {
        super.onAttach(pm)
        navView = this.pageView.addViewX(BottomNavigationView(context, null, bottomNavStyle), Params.relative.parentHor.parentBottom.heightWrap) {
//            backColor(Color.WHITE)
        }
        frameView = this.pageView.addViewX(FrameLayout(context), Params.relative.parentTop.parentHor.above(navView))

        container = TabPageContainer(pm.activity, this, frameView)
        for (item in items) {
            addItemToContainer(item)
        }
        if (currentTab in items.indices) {
            selectTab(currentTab)
        }
        navView.setOnNavigationItemSelectedListener {
            selByUser(it)
        }

    }


    class TabPageContainer(activity: BaseActivity, lifecycleOwner: LifecycleOwner, frameLayout: FrameLayout) : PageContainer(activity, lifecycleOwner, frameLayout) {

        init {
            onlyCurrentVisible = true
        }

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


}