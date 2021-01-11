package dev.entao.pages

import android.os.Bundle
import android.widget.LinearLayout
import dev.entao.page.BaseActivity
import dev.entao.views.TitleBarX
import dev.entao.views.*

/**
 * Created by entaoyang@163.com on 16/4/14.
 */

abstract class TitledActivity : BaseActivity() {
    lateinit var rootView: LinearLayout
    lateinit var titleBar: TitleBarX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = LinearLayout(this).needId().vertical()
        rootView.backColorWhite()
        setContentView(rootView)
        titleBar = TitleBarX(this)
        rootView.addView(titleBar, Params.linear.widthFill.height(TitleBarX.HEIGHT))

        onCreateContent(rootView)
    }


    abstract fun onCreateContent(contentView: LinearLayout)
}