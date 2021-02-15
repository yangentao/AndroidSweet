package dev.entao.hello

import android.os.Bundle
import dev.entao.page.StackActivity
import dev.entao.page.TabbarPage
import dev.entao.page.TabPageItem


class MainActivity : StackActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabPage = TabbarPage()
        tabPage.addTab(TabPageItem("A", R.mipmap.phone, TestPage()))
        tabPage.addTab(TabPageItem("B", R.mipmap.reg, TestPage2()))
        tabPage.selectTab(0)

        setContentPage(tabPage)

//        setContentPage(TestPage())


    }


}
