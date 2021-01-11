package dev.entao.hello

import android.os.Bundle
import dev.entao.page.PageActivity


class MainActivity : PageActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentPage(TestPage())


    }


}
