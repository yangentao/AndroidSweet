package dev.entao.appbase

import java.util.*

/**
 * Created by entaoyang@163.com on 2018-07-30.
 */


class MyTimer(var callback: (MyTimer) -> Unit = {}) {

	var timer: Timer? = null
	var paused: Boolean = false


	@Synchronized
	fun cancel() {
		timer?.cancel()
		timer = null
		paused = false
	}

	@Synchronized
	fun schedule(delay: Long) {
		cancel()
		val t = Timer(true)
		t.schedule(makeTask(), delay)
		this.timer = t

	}

	@Synchronized
	fun repeat(period: Long, delay: Long = period) {
		cancel()
		val t = Timer(true)
		this.timer = t
		t.scheduleAtFixedRate(makeTask(), delay, period)
	}

	private fun makeTask(): TimerTask {
		return object : TimerTask() {
			override fun run() {
				if (!paused) {
					callback.invoke(this@MyTimer)
				}
			}
		}
	}

}