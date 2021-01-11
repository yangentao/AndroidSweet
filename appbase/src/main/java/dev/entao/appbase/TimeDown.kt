package dev.entao.appbase

import java.util.*

/**
 * 用于验证码的倒计时
 * Created by yangentao on 2016-02-03.
 * entaoyang@163.com
 */
object TimeDown {
    const val MSG_TIME_DOWN = "msg.timedown"

    private val map = HashMap<String, Int>()

    fun start(edName: String, seconds: Int = 60) {
        map[edName] = seconds
        Task.countDown(seconds) { sec ->
            MSG_TIME_DOWN.fire {
                n1 = sec.toLong()
                s1 = edName
            }
            if (sec == 0) {
                map.remove(edName)
            }
            sec >= 0 && edName in map
        }
    }

    fun cancel(edName: String) {
        map.remove(edName)
    }
}
