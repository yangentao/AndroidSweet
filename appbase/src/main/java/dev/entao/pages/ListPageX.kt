package dev.entao.pages

import android.widget.RelativeLayout
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.entao.appbase.dp
import dev.entao.page.RelPage
import dev.entao.views.*

class ListPageX : RelPage(), ListCallback {
    lateinit var listView: ListViewX
        private set
    lateinit var emptyView: TextView
        private set
    lateinit var refreshLayout: SwipeRefreshLayout
        private set

    override fun onCreateContent(contentView: RelativeLayout) {
        super.onCreateContent(contentView)
        titleBar {
            title("ListPage")
        }
        contentView.apply {
            listView = listViewX {
                relativeParams {
                    parentFill
                }
            }
            emptyView = textView {
                relativeParams {
                    parentCenter.wrap
                }
                minimumWidth = 60.dp
                minimumHeight = 40.dp
                paddings(10)
                stylePrimaryText()
                gravityCenter()
                gone()
                text = "没有内容"
            }
            listView.emptyView = emptyView
        }

    }
}