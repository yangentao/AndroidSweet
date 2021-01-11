package dev.entao.ui.util

import android.widget.TextView
import dev.entao.appbase.Task
import dev.entao.appbase.resString
import dev.entao.log.logd
import dev.entao.ui.R
import java.util.*

/**
 * 用于验证码的倒计时
 * Created by yangentao on 2016-02-03.
 * entaoyang@163.com
 */
object TimeDown {

    private val map = HashMap<String, TextView>()
    private val secondsMap = HashMap<String, Int>()

    fun updateView(name: String, view: TextView) {
        map[name] = view
        view.isEnabled = !secondsMap.containsKey(name)
    }

    //要在主线程调用
    fun startClick(name: String, secondsLimit: Int, view: TextView) {
        logd("startClick ", name, secondsLimit)
        map[name] = view
        view.isEnabled = false
        if (!secondsMap.containsKey(name)) {
            secondsMap.put(name, secondsLimit)
            logd("倒计时开始", name, secondsLimit)

            Task.countDown(secondsLimit) { leftSec ->
                secondsMap.put(name, leftSec)

                val v = map[name]
                if (v != null) {
                    var s = "" + leftSec
                    s += R.string.yet_retrive_again.resString
                    v.text = s
                    if (leftSec == 0) {
                        secondsMap.remove(name)
                        v.isEnabled = true
                        v.text = R.string.yet_retrive.resString
                    }
                }
                true
            }
        } else {
            logd("重复的点击")
        }
    }
}
