@file:Suppress("MemberVisibilityCanBePrivate")

package dev.entao.pages

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.entao.appbase.Task
import dev.entao.page.LinearPage
import dev.entao.theme.heightEditSearch
import dev.entao.views.*

abstract class ListPage : LinearPage(), ListCallback, ListClickCallback {

    lateinit var listViewParent: RelativeLayout
        private set
    lateinit var listView: ListViewX
        private set
    lateinit var emptyView: TextView
        private set
    lateinit var refreshLayout: SwipeRefreshLayout
        private set

    var autoShowRefreshingAnimator = true
    var withSearchEdit = false
    var searchEdit: EditText? = null


    var emptyText = "没有内容"
    var loadingText = "加载中..."


    override fun onCreateContent(contentView: LinearLayout) {

        if (withSearchEdit) {
            contentView.editText {
                linearParams {
                    widthFill.heightEditSearch.margins(15, 5)
                }
                withClearButton()
                editStyleRound()
                singleLine()
                hint = "搜索"
                searchEdit = this
                afterChanged {
                    onSearchTextChanged(it)
                }
            }
        }

        contentView.relativeLayout {
            linearParams {
                widthFill.flexY
            }
            listViewParent = this
            refreshLayout = append {
                relativeParams {
                    fill
                }
                setOnRefreshListener {
                    onPullRefresh()
                }
                setColorSchemeResources(
                    android.R.color.holo_green_dark,
                    android.R.color.holo_blue_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_red_dark
                )
                listView = listViewX {
                    marginParams {
                        fill
                    }
                }
            }
            emptyView = textView(Params.relative.fill) {
                gravityCenter()
                stylePrimaryText()
                gone()
                textS = emptyText
            }
            listView.emptyView = emptyView

        }

        listView.callback = this
        listView.clickCallback = this

        enablePullRefresh(false)
    }

    fun enablePullRefresh(enable: Boolean = true) {
        refreshLayout.isEnabled = enable
    }


    override fun onItemsChanged() {
        setRefreshing(false)
    }


    protected open fun onSearchTextChanged(s: String) {

    }


    protected open fun onPullRefresh() {
        requestItems(null)
    }

    fun setRefreshing(refresh: Boolean) {
        Task.mainThread {
            refreshLayout.isRefreshing = refresh
            if (refresh) {
                emptyView.text = loadingText
            } else {
                emptyView.text = emptyText
            }
        }
    }

    fun setItems(items: List<Any>) {
        listView.setItems(items)
    }

    fun notifyDataSetChanged() {
        listView.notifyDataSetChanged()
    }

    fun getItem(position: Int): Any {
        return listView.getItem(position)
    }


    fun requestItems(args: Any?) {
        if (autoShowRefreshingAnimator) {
            setRefreshing(true)
        }
        Task.back {
            val ls = onRequestItems(args)
            Task.fore {
                if (autoShowRefreshingAnimator) {
                    setRefreshing(false)
                }
                setItems(ls)
            }
        }
    }

    protected open fun onRequestItems(args: Any?): List<Any> = emptyList()


}
