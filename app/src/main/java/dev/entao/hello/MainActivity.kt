package dev.entao.hello

import android.os.Bundle
import dev.entao.page.PageActivity
import dev.entao.page.TabPage
import dev.entao.page.TabPageItem


class MainActivity : PageActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabPage = TabPage()
        tabPage.addTab(TabPageItem("A", R.mipmap.phone, TestPage()))
        tabPage.addTab(TabPageItem("B", R.mipmap.reg, TestPage2()))
        tabPage.selectTab(0)

        setContentPage(tabPage)
//        setContentPage(TestPage())


    }


}
