package dev.entao.page

import android.view.MenuItem
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.entao.views.*


class TabPageItem(val title: String, val icon: Int, val page: Page)

class TabbarPage : Page() {
    var bottomNavStyle = com.google.android.material.R.style.Widget_MaterialComponents_BottomNavigationView
    lateinit var tabPageContainer: TabContainer
    lateinit var tabbarView: BottomNavigationView
    lateinit var frameView: FrameLayout
    private val items: ArrayList<TabPageItem> = ArrayList()
    private var currentTab: Int = 0

    fun setTabs(items: List<TabPageItem>) {
        this.items.clear()
        this.items.addAll(items)
        if (this.attached) {
            this.tabPageContainer.removeAll()
            tabbarView.menu.clear()
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
                tabbarView.menu.getItem(n).isChecked = true
                tabPageContainer.currentIndex = n
            }
        }
    }

    fun removeTab(n: Int) {
        if (n in items.indices) {
            val item = items.removeAt(n)
            if (attached) {
                tabPageContainer.removePage(item.page)
                val mid = tabbarView.menu.getItem(n).itemId
                tabbarView.menu.removeItem(mid)

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
        tabPageContainer.addPage(item.page)
        tabbarView.menu.add(item.title).apply {
            this.setIcon(item.icon)
        }
    }

    private fun selByUser(mitem: MenuItem): Boolean {
        for (n in items.indices) {
            if (items[n].title == mitem.title) {
                tabPageContainer.currentIndex = n
                return true
            }
        }
        return true
    }

    override fun onAttach(pm: StackContainer) {
        super.onAttach(pm)
        tabbarView = this.pageView.addViewX(BottomNavigationView(context, null, bottomNavStyle), Params.relative.parentHor.parentBottom.heightWrap) {
//            backColor(Color.WHITE)
        }
        frameView = this.pageView.addViewX(FrameLayout(context), Params.relative.parentTop.parentHor.above(tabbarView))

        tabPageContainer = TabContainer(this, frameView)
        for (item in items) {
            addItemToContainer(item)
        }
        if (currentTab in items.indices) {
            selectTab(currentTab)
        }
        tabbarView.setOnNavigationItemSelectedListener {
            selByUser(it)
        }

    }


}